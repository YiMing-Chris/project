# 一、项目要求：

## 作业名称

《抢选课系统》
[CSDN地址](https://blog.csdn.net/Chris_Eli/article/details/125706455?csdn_share_tail=%7B%22type%22%3A%22blog%22%2C%22rType%22%3A%22article%22%2C%22rId%22%3A%22125706455%22%2C%22source%22%3A%22Chris_Eli%22%7D&ctrtid=32no5)

## 项目描述

设计并开发一个抢选课系统，可以支持用户登录、浏览课程、创建抢课详情等功能，另外还需要实现学生抢课的功能设计，能应对万名学生的并发使用，对出现的各种并发问题进行解决，并在Linux服务器上部署。项目利用JMeter工具进行压力测试，对比了采用缓存、消息队列等手段对于提高系统响应速度并发能力的效果。

## 要实现的接口和功能

- 登录、注册、注销
- 显示课表详情、显示选课详情
- 抢课、查看抢课结果
- 项目部署、压测

## 工具栈

- Redis
- RabbitMQ
- MySQL
- SpringBoot
- Maven、Git、Docker
- Jmeter、Postman

在学生抢课的过程中，为了减轻数据库的压力，节省数据库资源，通过自定义限流注解、使用内存、Redis形成多级缓存来减少到达MQ的流量；通过RabbitMQ解决异步返回抢课结果的问题，并对流量进行削峰，从而降低数据库的流量压力；使用SpringBoot框架来降低开发难度，减轻代码负担；使用Maven管理项目、使用Git管理代码、使用Docker部署项目;使用Jmeter进行并发压测，使用Postman进行接口测试。

|     |     |     |
| --- | --- | --- |
| 1w并发量 | 场景  | 系统吞吐量提高 |
| 使用限流注解 | 访问课程列表 | 791% |
| 使用消息队列 | 抢课  | 95% |
| 使用Redis缓存 | 抢课  | 8%  |
| 使用内存缓存 | 抢课  | 28% |
| SQL优化 | 抢课  | 15% |

## 整体流程
![`在这里插入图片描述`](https://img-blog.csdnimg.cn/2554c9434c734baf9976fdd58c4ff256.png )


## 代码结构

```txt
GrabCourses
├─ MainApplication.java 
├─ annotation # 自定义限流注解
│	└─ AccessLimit.java 	
├─ config # 相关中间件配置
│	├─ RabbitMQConfig.java # 
│	├─ RedisConfig.java
│	└─ WebConfig.java
├─ context # 用于封装当前用户
│	└─ UserContext.java
├─ controller # 负责协调各部件完成任务
│	├─ courses
│	│	├─ CoursesController.java
│	│	└─ SecKillController.java
│	└─ user
│	 	├─ LoginController.java
│	 	└─ RegisterController.java
├─ dao # 数据访问层
│	├─ CoursesDao.java
│	├─ OrderDao.java
│	└─ StudentDao.java
├─ domain # 实体类
│	├─ Courses.java
│	├─ OrderInfo.java
│	├─ SecKillCourses.java
│	├─ SecKillOrder.java
│	└─ Student.java
├─ exception # 管理全局异常
│	├─ GlobalException.java
│	└─ GlobalExceptionHandler.java
├─ interceptor # 全局拦截器
│	└─ AccessInterceptor.java
├─ message # 封装消息
│	└─ SecKillMessage.java
├─ mq # 消息中间件
│	├─ MQReceiver.java
│	└─ MQSender.java
├─ redis # 运用模板设计模式命名Key
│	├─ KeyPrefix.java
│	├─ BasePrefix.java
│	├─ AccessLimitKey.java
│	├─ CoursesKey.java
│	├─ OrderKey.java
│	└─ StudentKey.java
├─ resolver # 自定义方法参数解析器
│	└─ UserArgumentResolver.java
├─ result # 封装返回类型和错误代码
│	├─ CodeMsg.java
│	└─ ServerResponse.java
├─ service  
│	├─ CoursesService.java
│	├─ OrderService.java
│	├─ SecKillService.java
│	└─ StudentService.java
├─ utils # 工具类
│	├─ MD5Util.java
│	├─ UUIDUtil.java
│	└─ ValidateSaltUtil.java
└─ vo  # 封装传输数据
 	├─ CoursesVO.java
 	└─ LoginInfoVO.java
```

# 二、功能实现

## 1.用户模块

### 【两次MD5密码加密】

1.  客户端登录时避免明文密码在网络中传输，所以在客户端界面直接进行第一次MD5;
2.  MD5的密码传输至服务端时，需要随机生成salt进行二次MD5，保存salt和两次MD5结果至数据库中。

### 【分布式Session】

1.  UUID方式生成Token，Redis保存(前缀+Token)-Student的键值信息模拟Session;
2.  将Token写到Cookie中跟随Response返回，设置Path为顶级域名之下。

### 【注册登录功能实现】

1.  封装服务端响应对象 ServerResponse 以及状态码消息对象 CodeMsg ;
2.  实现用户登录，批量注册学生用户逻辑;
3.  自定义方法参数解析器用于获取请求中包含的Token值，并查询Redis封装成User。

### 【参数校验】

1.  前端直接对输入的账号密码长度进行校验；
    
2.  后端使用JSR和Hibernate提供的校验注解对前端传入参数进行校验。
    

### 【登录次数校验】

1.  对所有请求进行拦截，如果接口含有自定义限流注解，则读取注解的设定值；
2.  并按照设定值将该用户的Key存储到Redis，Value为登录次数，设置存活时间，存活时间内登录次数如果超过设定值，则拒绝后续访问。

* * *

## 2.课程模块

### 【展示课程列表及详情】

1.  插入课程表数据；
2.  通过连接普通课程表和抢课课程表，查询出抢课课程的全部信息；
3.  将所有课程表展示在courses_list中，单个课程的全部信息展示在课程详情页courses_detail中。

### 【抢课程】

1.  通过Spring声明式事务,保证减少课程容量、创建普通课程情况以及创建抢课课程情况三步的原子性；
2.  抢课倒计时刷新由前端完成，后端仅仅在查看商品详情时返回一次计算的剩余时间即可，保证所有客户端的秒杀时间能够同步，以后端为准。

# 三、项目重难点的思考分析与解决

## 【RabbitMQ】

### 【消息队列削峰】

- *使用消息队列对短时间内的大流量进行削峰，此时消息队列内有大量消息，如果不及时处理队列中的消息，会引发消息过期、消息处理慢、RabbitMQ负载压力大等问题，所以要想办法把不必要的消息在进入消息队列之前剔除。*
    
    - 通过多级缓存减少进入消息队列的流量。
    - 增加消费者处理消息。

### *【消息丢失问题】*

- *消息丢失是消息中间件老生常谈的问题，在设计时必须考虑到。消息丢失主要发生在涉及消息传递的过程中，包括生产者->交换机，交换机->队列，队列->消费者，也可能因为消息过期、RabbitMQ重启、消费者未成功消费原因导致。*
- *但是实际上，对于100人、500人、1000人，5000人抢一节30容量的课的情况来说，抢到的人占少数，抢不到课才是理所应当的，所以对于一些同学的抢课请求消息偶然的丢失，其实是可以无视的，但是为了维护系统可靠性，我还是决定对该问题进行处理。*
    - 我使用了生产者确认模式、消息与队列持久化、消费确认、建立死信队列处理过期消息来解决该问题。

* * *

## 【Redis】

### 【缓存穿透问题&多级缓存】

- *大流量的访问Redis缓存，如果Redis不存在相对应的Key，那么所有请求都会落在数据库上，造成数据库压力大直至崩溃。*
- *因为界面上显示能点击进行抢课的课程都已经预先加载在Redis中了，所以理论上不存在缓存穿透的问题。*
- *但是为了预防一些不法分子故意访问界面不存在的课程来攻击系统，所以在设计时还是需要考虑该问题。*
    - Redis缓存：可以在Redis的前面上再加上一个过滤器来减轻Redis压力，考虑使用布隆过滤器或者内存缓存。使用布隆过滤器的话只能判断课程是否存在而且可能误判，但是无法判断课程是否被抢光；而使用内存缓存的话，对于不存在和已经抢光的课程都可以在访问Redis之前拒绝访问,因此我采取使用内存缓存的方案。
        
    - 内存缓存实现：在内存中维护一个HashMap，用Key存储课程ID，Value存储是否抢光，如果Redis中判断课程抢光，修改HashMap中的Value为true，后续的请求就全被内存缓存拦截，减轻了Redis的压力。
        
        * * *
        

### 【缓存击穿&缓存雪崩问题】

- *通常情况，我们会为缓存设置一个过期时间。而如果在一个资源的缓存过期以后（或者还未来得及缓存），瞬间涌入大量查询该资源的请求会一股脑的奔向数据库，数据库可能秒秒钟挂掉。这种情况我们称之为缓存击穿。*
- *Redis 中大量的 key 同时失效，这时大量请求会一股脑的奔向数据库，数据库可能秒秒钟挂掉，这种情况我们称之为缓存雪崩。*
    - 在本项目中，大量的请求访问的都是课程余量和抢课记录，如果课程余量或者抢课记录的Key过期，大量请求就会访问数据库。因此在Redis中加入课程库存和抢课记录的的Key时，将Key设置为永不过期，这样可以避免缓存过期失效导致的缓存击穿和雪崩问题。
        
        * * *
        

### 【Redis缓存淘汰问题】

- *进一步思考，将缓存设置为永不过期时，需要考虑Redis的缓存容量问题，大量存在永不过期的缓存是否会导致Redis缓存容量不足？*
    - 在本项目中，课程容量和抢课记录的Key采用String数据类型，为方便计算，假设每节课的容量都相等，理论上来说，有多少个开放抢课的课程，就会建立多少个课程容量的Key；有多少位学生抢到课程，就会创建多少条抢课记录，因此，课程容量和抢课记录的Key总数量 = 开放抢课的课程数 + 开放抢课的课程数*开放抢课的课程容量  。数据量不算大，因此在本项目的环境中，不会造成存在大量永不过期的缓存导致Redis缓存不足。
        
        * * *
        

### 【课程超选问题】

- *我通过对SQL语句和运行日志的分析，发现是多线程并发更新数据库课程余量导致的问题。多个线程同时读取课程的余量，其中一个线程读取余量后减1，此时余量已经为0了，但其他线程读取的余量未及时更新，在实际课程余量已经不足的情况下，余量依然在减，导致该课程余量为变为负数，选课数超过抢课的预期。*
    - *使用MySQL悲观锁，在select课程余量的时候加入for update，这样保证不会有多个线程同时读取课程余量，这样就保证了扣减库存的操作串行执行*
        - 这个方式并发效率低，并且如果释放锁过程出现问题，容易导致死锁问题。
    - *使用MySQL乐观锁，加入对版本号的更新和判断*
        - 这个方式在高并发情况下会出现大量的版本冲突和重试，占用和浪费了CPU性能，影响吞吐量。
    - *使用Synchonized锁住读取课程余量和减少课程余量的代码块，代码量简单，只需要加入Synchonized关键字*
        - 这个方式并发效率低，并且只能在单JVM中起作用，不支持分布式系统。
    - *使用Redis分布式锁*
        - 这个方式需要解决Redis分布式锁带来的一系列问题：加锁和设置过期时间的原子性、锁超时的续约问题等等，增大了代码难度。
    - *使用Where条件，直接在课程余量的update语句中加入courses_stock > 0 的判断*
        - 这个方式实现简单，并且在压测过程中一直保持有效，采用该方法，可以配合unsigned非负字段限制使用，进一步保障余量不为负数。
- *因为采用了Where条件方式，在数据库层面解决超卖问题。所以需要减少到达数据库的流量来降低数据库的压力。可以在请求到达MySQL之前，使用Redis进行余量预减，在余量已经为负的情况下把不必要达到数据库的请求进行拦截*
    - 可以使用Redis来进行缓存预热,在服务启动时将数据库中课程ID作为Key，余量作为Value，想要对数据库进行操作前，先对Value-1，判断Value大于0后才放行进入数据库进行操作。这里可以用Redis的decr操作来保证Redis的原子性。
        
        * * *
        

### 【Redis和数据库一致性问题】

- *在本项目中，会出现Redis中的课程余量与数据库中的课程余量不一致的问题。我们无需顾虑Redis中的课程余量是否与数据库同步，他的作用仅仅只是为了阻挡多余的请求透穿到DB，起到一个保护的作用，它类似于一个挡箭牌，帮我们阻挡住那些不必要到达数据库的请求。真正且正确的课程余量位于数据库中。*
    
    * * *
    

### 【用Redis存储Session】

- *生成Session*
    
    - 当用户登陆时，服务器生成一个全局唯一的字符串`SESSION:模板前缀+token`做为redis中String数据结构的Key名，然后将Token加入Cookie返回给客户端。 之后该用户的后续请求都会带上此Cookie， 我们编写一个AccessInterceptor类，其作用为读取请求中的Cookie中的Token，从Redis中取出该Token对应的数据，然后放到ThreadLocal中以供后续使用。
- *Session过期*
    
    - 使用Redis自带的过期功能expire为Session设置过期时间，默认设置为1天。
- *Session更新*
    
    - 在Redis查询完Session之后，如果Key存在，刷新过期时间。

* * *

### 【ThreadLocal】

- *每一个用户的Http请求对应一个线程，每个线程都有自己的用户信息，我们希望这些用户信息能够实现数据隔离，在对本线程的用户数据进行修改时不会影响到别的线程的用户数据。*
    - 使用ThreadLocal存储当前线程的用户信息。
***

### 【管理Key】

 - *Redis缓存中存储着各种各样的Key:用户的Key、课程信息的Key、选课结果的Key、限流注解的Key，而且这些Key有共同的功能也有不同的功能，如果不加以规范，那么这些Key将会杂乱无章，难以管理*
 	- 使用模板设计模式
    - 在KeyPrefix中声明两个方法:过期时间expireSeconds()和获取key的前缀getPrefix()。在BasePrefix中实现该方法。
    - 在UserKey、OrderKey、CourseKey、AccessLimitKey中继承BasePrefix，并提供根据模板格式实现自己的Key。 
  	

# 四、项目优化

### 【接口流量限制&防刷】

- *用户在抢课的时候大概率会对页面进行疯狂刷新，每次刷新都会对后端的接口进行访问，这无疑给后台系统带来了很大的压力，需要拒绝频繁、恶意刷新用户的请求。*
    - 经过Jmeter压测,实现接口限流后吞吐量提高962.5%!
    - 自定义实现注解AccessLimit，放在需要进行流量限制的接口。利用Spring提供的拦截器对每一个请求方法进行判断，是否包含限流注解。
    - 对包含限流注解的方法用Redis存储设定时间内的访问次数，如果超过规定的访问次数，拒绝该用户的访问。

* * *

### 【内存缓存优化】

 - 原本使用HashMap作为内存缓存。但是在压测过程中是高并发的，而HashMap是线程不安全的，虽然压测中使用HashMap并没有出现线程安全问题，但还是将其改为线程安全的ConcurrentHashMap。
 - 在初始化ConcurrentHashMap时，根据课程数量直接预设置ConcurrentHashMap大小，避免频繁扩容增大消耗。
 * **
### 【ThreadLocal优化】
 - *ThreadLocal存在内存泄露问题，需要优化*
 	- 使用完ThreadLocal都调用它的remove()方法清除数据
 	- 将ThreadLocal变量定义成private static，这样就一直存在ThreadLocal的强引用，也就能保证任何时候都能通过ThreadLocal的弱引用访问到Entry的value值，进而清除掉 。
***

### 【SQL索引优化】

- *在InnoDB的UPDATE语句中，如果WHERE后面的条件未包含索引列，那么UPDATE执行时就会对所有记录加记录锁 + 间隙锁（相对于锁住全表），并且对全表进行扫描；我们需要对其进行优化*
    - 为WHERE条件增加索引，在courses_id上加上唯一索引
    - 经过EXPLAIN测试，type类型从index变为range
    - 经过Jmeter压力测试，抢课系统吞吐量提高115%

```sql
UPDATE qiangke_courses 
SET stock_count = stock_count - 1 
WHERE
    courses_id = #{CoursesId} AND stock_count > 0
```

* * *

- 在SELECT语句中，为WHERE条件增加索引，在order_info中为courses_id和student_id加上索引；
- 将*替换为具体字段，减少传输数据的大小；
    - 经过EXPLAIN测试，搜索行数减少81.5%
    - type从ALL优化为ref

```sql
EXPLAIN SELECT
    student_id,nickname,courses_name,create_date
FROM
    qiangke_student qs
    INNER JOIN order_info oi ON qs.id = oi.student_id 
WHERE
    oi.courses_id = 3;
```

* * *

- 在qiangke_order中为student_id和courses_id加上唯一组合索引
    - 将type从ALL优化为const

```sql
EXPLAIN SELECT
    * 
FROM
    qiangke_order 
WHERE
    student_id = 19191234
    AND courses_id = 1
```
### 【可以优化的地方】

 

 1. 可以进阶架构为分布式架构，构建微服务，并升级缓存为分布式缓存
 2. 通过Docker容器同时启动多个该项目，使用Nginx进行消息分发、负载均衡
 3. 使用Redis分布式锁来保持Redis缓存与数据库中的课程余量一致


# 五、个人收获

### 【理解了模板设计模式】
### 【理解了Redis缓存相关问题的解决】
###  【理解了RabbitMQ消息丢失问题的解决】
###  【理解了ThreadLocal的原理和内存泄露的解决】
###  【理解了HashMap和ConcurrentHashMap的区别和原理】
###  【了解了数据库索引调优和事务】
###  【体验了项目架构、开发、测试、部署、压测、监控、调优流程】



# 六、项目压测数据


### 【电脑配置】

|     |     |
| --- | --- |
| Processor | AMD Ryzen 5 4600U with Radeon Graphics (12 CPUs), ~2.1GHz |
| Memory | 16384MB RAM |
| Operating System | Windows 10 家庭中文版 64-bit |

### 【压测数据表】

|     |     |     |     |     |     |     |     |     |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| 课程容量 | 人数  | 压测场景 | 内存缓存 | Redis缓存 | 消息队列 | 使用索引 | 吞吐量 | 相较提高 |
| 30  | 10000 | 抢课  | 开启  | 开启  | 开启  | 是   | 1006.7/sec | 23% |
| 30  | 10000 | 抢课  | 开启  | 开启  | 开启  | 否   | 813.5/sec | 28% |
| 30  | 10000 | 抢课  | 关闭  | 开启  | 开启  | 否   | 632.3/sec | 8%  |
| 30  | 10000 | 抢课  | 关闭  | 关闭  | 开启  | 否   | 582.4/sec |     |
| 100 | 10000 | 抢课  | 开启  | 开启  | 开启  | 否   | 699.8/sec | 17% |
| 100 | 10000 | 抢课  | 关闭  | 开启  | 开启  | 否   | 594.6/sec |     |
|     | /   |     |     |     |     |     |     |     |
| 30  | 10000*5 | 抢课  | 开启  | 开启  | 开启  | 否   | 877/sec |     |
| 30  | 10000*5 | 抢课  | 开启  | 开启  | 开启  | 是   | 1016.3/sec | 15% |
|     | /   |     |     |     |     |     |     |     |
| 30  | 5000 | 抢课  | 开启  | 开启  | 开启  | 否   | 700.2/sec |     |
| 100 | 5000 | 抢课  | 开启  | 开启  | 开启  | 否   | 600.6/sec |     |
| 500 | 5000 | 抢课  | 开启  | 开启  | 开启  | 否   | 594.7/sec |     |
| 1000 | 5000 | 抢课  | 开启  | 开启  | 开启  | 否   | 569.3/sec |     |
| 5000 | 5000 | 抢课  | 开启  | 开启  | 开启  | 否   | 588.9/sec |     |
|     |     |     | 限流注解 |     |     |     |     |     |
| /   | 100 | 访问课程列表 | 开启  | /   | /   | /   | 854.7/sec | 862.5% |
| /   | 100 | 访问课程列表 | 关闭  | /   | /   | /   | 88.8/sec |     |
| /   | 10000 | 访问课程列表 | 开启  | /   | /   | /   | 703/sec | 691.6% |
|     |     |     |     |     | 消息队列 |     |     |     |
| 30  | 10000 | 抢课  | 开启  | 开启  | 开启  | 否   | 903.2/sec | 95% |
| 30  | 10000 | 抢课  | 开启  | 开启  | 关闭  | 否   | 462.7/sec |     |
|     |     | /   |     |     |     |     |     |     |
| /   | 10000 | 登录  | /   | /   | /   |     | 176/sec |     |

# 项目架设和压测过程

### 1.通过maven打包出jar包，然后通过finalshell放到云服务器上

![在这里插入图片描述](https://img-blog.csdnimg.cn/a85da38ff92945ada766f0cb77d81743.png  =500x)


![在这里插入图片描述](https://img-blog.csdnimg.cn/46b0737f139c4f1eafc9827854fbb68d.png =500x)

### 2.编写dockerfile

```shell
# 基础镜像
FROM java:8
# 挂载点为/tmp，jar包就会存在这里
VOLUME /tmp
# 拷贝打包好的jar包
COPY GrabCourses-springboot.jar GrabCourses-springboot.jar
# 暴露端口
EXPOSE 8080
# 容器创建后执行jar
ENTRYPOINT ["java","-jar","/GrabCourses-springboot.jar"]
```

### 3.通过dockerfile创建镜像

`docker build -t chris/grabcourses:1.0 .`

```shell
[root@VM-12-2-centos docker-GrabCourses]# docker build -t chris/grabcourses:1.0 .
Sending build context to Docker daemon  42.3 MB
Step 1/5 : FROM java:8
 ---> d23bdf5b1b1b
Step 2/5 : VOLUME /tmp
 ---> Using cache
 ---> f019b8640c21
Step 3/5 : COPY GrabCourses-springboot.jar GrabCourses-springboot.jar
 ---> c41c9a11288d
Removing intermediate container b76f3bc2d10b
Step 4/5 : EXPOSE 8080
 ---> Running in 3c1a4378d8d4
 ---> a239e67a7969
Removing intermediate container 3c1a4378d8d4
Step 5/5 : ENTRYPOINT java -jar /GrabCourses-springboot.jar
 ---> Running in b3ebf9f2963e
 ---> b60978817fcf
Removing intermediate container b3ebf9f2963e
Successfully built b60978817fcf
```

### 4.成功启动项目

`docker run -d -p 8083:8080 --name chris-qiangke -e TZ=Asia/Shanghai chris/grabcourses:1.0`

```shell
[root@VM-12-2-centos docker-GrabCourses]# docker run -d -p 8083:8080 --name chris-qiangke -e TZ=Asia/Shanghai chris/grabcourses:1.0
7e728b22fac7cf3919f9f5a01f6ea338556e32a6e6cd5e3573a166bf7ec6ce64
[root@VM-12-2-centos docker-GrabCourses]# docker ps
CONTAINER ID        IMAGE                   COMMAND                  CREATED             STATUS              PORTS                                                                                                         NAMES
7e728b22fac7        chris/grabcourses:1.0   "java -jar /GrabCo..."   4 seconds ago       Up 3 seconds        0.0.0.0:8083->8080/tcp                                                                                        chris-qiangke
079d5ce45451        mysql:5.7.23            "docker-entrypoint..."   5 days ago          Up 5 days           0.0.0.0:3306->3306/tcp, 33060/tcp                                                                             e3-mall-mysql
ce39e25fa82c        redis:3.2               "docker-entrypoint..."   5 days ago          Up 5 days           0.0.0.0:6379->6379/tcp                                                                                        e3-mall-redis
4790844cf64c        rabbitmq:management     "docker-entrypoint..."   6 days ago          Up 6 days           4369/tcp, 5671/tcp, 0.0.0.0:5672->5672/tcp, 15671/tcp, 15691-15692/tcp, 25672/tcp, 0.0.0.0:15672->15672/tcp   Myrabbitmq
```

### 5.通过预留的接口批量注册学生账号
![在这里插入图片描述](https://img-blog.csdnimg.cn/72462e33af3c4e3880f073484e62769c.png  =500x)


### 6.登录

![在这里插入图片描述](https://img-blog.csdnimg.cn/2d658ce9827e45f9ae3c59b5485b8961.png =500x)


### 7.进入课程界面，点击详情

![在这里插入图片描述](https://img-blog.csdnimg.cn/59055ae6a1394a4f9f7cb247a3e84b93.png =500x)


### 8.点击立即选课

![在这里插入图片描述](https://img-blog.csdnimg.cn/a8699d4e9885461d90e83c56ae0e5be2.png =500x)


### 9.跳转到等待界面

![在这里插入图片描述](https://img-blog.csdnimg.cn/3950209beca947d08e30b7c758a8dcbc.png =500x)


### 10.返回抢课结果和已选课的列表

![在这里插入图片描述](https://img-blog.csdnimg.cn/65d001d10bde4d9aaa554c20021b3b0e.png =400x)


### 11.通过docker logs -f 容器ID命令查看项目运行日志

![在这里插入图片描述](https://img-blog.csdnimg.cn/2e5dbea2507a40cc988eeae80e02e4e3.png =500x)


### 12.通过Redis Desktop Manager查看生成的缓存

![在这里插入图片描述](https://img-blog.csdnimg.cn/e61ad1de6f654b4c841e1b0412a71756.png =500x)


### 13.通过Navicat查看数据库
![在这里插入图片描述](https://img-blog.csdnimg.cn/41dea26b95754b90b4a921f33dd87cfb.png =500x)


### 14.如果重复选课，会返回失败

![在这里插入图片描述](https://img-blog.csdnimg.cn/692e5e21fef0425faec0d1db48077ce0.png =200x)


### 15.选课时间结束，界面置灰，无法点击

![在这里插入图片描述](https://img-blog.csdnimg.cn/752c24e83c174fa09f046f2a24ae2104.png =500x)


# 压力测试过程

## 压测10000位用户同时登录

### 1.通过预留接口生成10000名学生的账号和第一次加密后的密码，放入txt文件，用于登录
    
![在这里插入图片描述](https://img-blog.csdnimg.cn/4f0b22dfd7d1451f92313e44aa246ff2.png =400x)


### 2.配置http请求默认值和请求信息头

### 4.导入已经生成好的账号密码txt

![在这里插入图片描述](https://img-blog.csdnimg.cn/db9152b461274c4c94980c31b6c7d9d0.png =500x)


### 5.通过正则表达式将服务器返回的token保存到本地txt文件

![在这里插入图片描述](https://img-blog.csdnimg.cn/9b315f64ccc3497ab04c00eeea8134de.png =500x)
![在这里插入图片描述](https://img-blog.csdnimg.cn/aa7841269c09446fb90306ed293a5f03.png =500x)


### 6.得到token的txt文件

![在这里插入图片描述](https://img-blog.csdnimg.cn/2f9312cdb1884c399946eee57fe71ff8.png =400x)


### 8.检查redis，成功生成10000位用户Key，多出来的10000是之前测试生成的用户我没删除。。

![在这里插入图片描述](https://img-blog.csdnimg.cn/bf01c20fc38640e581349e71a3d9740f.png =500x)


### 9.对比Redis和数据库里的数据，无误

![在这里插入图片描述](https://img-blog.csdnimg.cn/9214af6bb9814ab58ee82ee0edf31b49.png =500x)

### 9.10000人登录

![在这里插入图片描述](https://img-blog.csdnimg.cn/5b8d9bb9b721420ea864b926728b36e8.png =500x)


### 10.10000人访问课程界面

![在这里插入图片描述](https://img-blog.csdnimg.cn/d5276298d1ad4b26aa2678149858a43a.png =600x)


### 11.10000人抢容量为30的课程

关闭内存缓存

![在这里插入图片描述](https://img-blog.csdnimg.cn/0d3df4c2947f44dda825487b5277747c.png =600x)


开启内存缓存

![在这里插入图片描述](https://img-blog.csdnimg.cn/f62fbaa0c0744a4d96f56c3577e3045c.png =600x)


### 12.10000人抢容量为100的课程

关闭内存缓存

![在这里插入图片描述](https://img-blog.csdnimg.cn/8b69ce02440a4a1ca9cd5a29ccac7f53.png =600x)


开启内存缓存

![在这里插入图片描述](https://img-blog.csdnimg.cn/4b891440c3cf40de8b8dbf3ecbb24789.png =600x)


### 13.使用缓存对消息队列削峰的效果

![在这里插入图片描述](https://img-blog.csdnimg.cn/4d54b76b042f44468fd968f06a42f9ca.png =500x)


### 14.使用消息队列的效果

关闭消息队列

![在这里插入图片描述](https://img-blog.csdnimg.cn/ce8edc9dc9cf44f7a198055e7db7b710.png =600x)


开启消息队列

![在这里插入图片描述](https://img-blog.csdnimg.cn/2129e8a1b6134a22a854d54fbe78938b.png =600x)


### 15.使用限流注解的效果

关闭注解

![在这里插入图片描述](https://img-blog.csdnimg.cn/eaa5bb28eb0146628754b61689cc41c5.png =600x)


开启注解

![在这里插入图片描述](https://img-blog.csdnimg.cn/c2b70cf7f02740b490c511dbee1ebd80.png =600x)


### 16.优化SQL

### 根据课程ID查询选择这门课的学生ID、姓名、课程名称、选课日期

```sql
EXPLAIN SELECT
    student_id,nickname,courses_name,create_date
FROM
    qiangke_student qs
    INNER JOIN order_info oi ON qs.id = oi.student_id 
WHERE
    oi.courses_id = 3;
```

添加索引前

![在这里插入图片描述](https://img-blog.csdnimg.cn/22903e5e58c349829db5163e17a8a060.png#pic_center)


添加索引后

![在这里插入图片描述](https://img-blog.csdnimg.cn/df7bfd6e78794bfab906be1f4601be85.png#pic_center)


### 根据学号和课程ID从数据库中查询选课记录，用于判断是否已经完成选课

```sql
EXPLAIN SELECT
	student_id,
	courses_id 
FROM
	qiangke_order 
WHERE
	student_id = 19191234 
	AND courses_id = 1
```

添加索引前

![在这里插入图片描述](https://img-blog.csdnimg.cn/14cf07886fb24778816b3fe67a9e83c2.png#pic_center)


添加组合索引后

![在这里插入图片描述](https://img-blog.csdnimg.cn/bd702573d7f64cbcb66f41e1cf0746cb.png#pic_center)


优化查询字段后（取消*查询）

![在这里插入图片描述](https://img-blog.csdnimg.cn/cb60b0af9176400f9a895b9037bc4248.png#pic_center)


将索引类型由普通索引变为唯一索引后

![在这里插入图片描述](https://img-blog.csdnimg.cn/71abce0b7eef4a5b92f8863d93b25e38.png#pic_center)


# 项目架设中遇到的问题

###  登录过程中出现报错
通过docker logs 查看错误信息，提示loginInfoVO中的id为null

```java
Field error in object 'loginInfoVO' on field 'id': rejected value [null];

codes [NotNull.loginInfoVO.id,NotNull.id,NotNull.java.lang.String,NotNull];

arguments [org.springframework.context.support.DefaultMessageSourceResolvable: codes [loginInfoVO.id,id]; arguments [];

default message [id]]; default message [不能为null]
```

经检查发现，在LoginInfoVO类中对Id和Password添加了@NotNull注解，但未对Id和Password提供get和set方法，所以登录过程中后端得到的Id和Password为null，故报错。
***
###  访问项目网页时报空指针异常
项目正常启动，当访问任何页面时，报了自定义的服务端异常错误。查看了项目log，发现报了空指针异常
![在这里插入图片描述](https://img-blog.csdnimg.cn/bb08a1c9b7e54c3da08c6a97ce700944.png =600x)

```java
2022-06-26 13:36:41.407 ERROR 40652 --- [nio-8081-exec-2] y.c.G.exception.GlobalExceptionHandler   : null
2022-06-26 13:36:41.407 ERROR 40652 --- [nio-8081-exec-2] y.c.G.exception.GlobalExceptionHandler   : java.lang.NullPointerException
```
使用Exception自带的堆栈追踪进行定位

```java
e.printStackTrace();

java.lang.NullPointerException
	at yiming.chris.GrabCourses.interceptor.AccessInterceptor.preHandle(AccessInterceptor.java:54)
```
定位到是preHandle方法中的语句出现逻辑漏洞，我当时想要在控制台打印出当前线程的用户ID，就在方法中添加了如下语句

> System.out.println("当前线程用户: " +UserContext.getStudent().getId());
> 
因为preHandle是全局拦截器，我没加上拦截作用范围，导致在登录的时候就拦截了请求，此时就需要输出当前线程的用户ID，然而用户还没登录，ID自然为空...所以报了空指针异常

解决方案是对拦截器设置排除拦截范围

```java
.excludePathPatterns("/user/**");
```
