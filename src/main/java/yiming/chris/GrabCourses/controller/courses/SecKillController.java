package yiming.chris.GrabCourses.controller.courses;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import yiming.chris.GrabCourses.domain.SecKillOrder;
import yiming.chris.GrabCourses.domain.Student;
import yiming.chris.GrabCourses.message.SecKillMessage;
import yiming.chris.GrabCourses.mq.MQSender;
import yiming.chris.GrabCourses.redis.CoursesKey;
import yiming.chris.GrabCourses.result.CodeMsg;
import yiming.chris.GrabCourses.service.CoursesService;
import yiming.chris.GrabCourses.service.OrderService;
import yiming.chris.GrabCourses.service.SecKillService;
import yiming.chris.GrabCourses.vo.CoursesVO;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ClassName:SecKillController
 * Package:yiming.chris.GrabCourses.controller.courses
 * Description:
 *
 * @Author: ChrisEli
 */
@Controller
@RequestMapping("/grab")
@Slf4j
public class SecKillController implements InitializingBean {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    volatile Long nowStock;

    @Autowired
    private CoursesService coursesService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SecKillService secKillService;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 内存标记Map，key为商品id，value为是否秒杀结束，用于减少对Redis的访问
     * 该内存标记非线程安全，但不会影响功能，只是有多个线程多次复写某商品卖完
     */
    private HashMap<Long, Boolean> coursesSecKillOverMap = new HashMap<>();

    @RequestMapping("/grab")
    public String asyncSecKill(Model model, Student student, @RequestParam("coursesId") Long coursesId) {
        if (student == null) {
            return "login";
        }
        model.addAttribute("user", student);

//         先访问内存标记，查看是否卖完或者存在
        if (coursesSecKillOverMap.get(coursesId) || !coursesSecKillOverMap.containsKey(coursesId)) {
            model.addAttribute("errorMsg", CodeMsg.SECKILL_OVER.getMsg());
            log.info(student.getId() + "：被内存缓存阻挡");
            return "kill_fail";
        }


        // 判断是否已经秒杀到商品，防止一人多次秒杀成功
        SecKillOrder order1 = orderService.getSecKillOrderByStudentIdAndCoursesId(student.getId(), coursesId);
        if (order1 != null) {
            model.addAttribute("errorMsg", CodeMsg.SECKILL_REPEAT.getMsg());
            return "kill_fail";
        }

        // Redis预减容量
        nowStock = redisTemplate.opsForValue().decrement(CoursesKey.courseStockKey.getPrefix() + ":" + coursesId);
        logger.info(student.getId() + " 抢课程 " + coursesId + "预减完Redis当前余量为：" + nowStock);

        // 如果库存预减完毕，则直接返回秒杀失败
        if (nowStock < 0) {
            // 记录当前商品秒杀完毕
            coursesSecKillOverMap.put(coursesId, true);
            model.addAttribute("errorMsg", CodeMsg.SECKILL_OVER.getMsg());
            log.info(student.getId() + "：被Redis缓存阻挡");
            return "kill_fail";
        }

         //正常进入秒杀流程：入队进行异步下单
        mqSender.sendSecKillMessage(new SecKillMessage(student, coursesId));
/**       *关闭消息队列后
 //判断容量
 CoursesVO course = coursesService.getCoursesDetailById(coursesId);
 if (course.getStockCount() <= 0) {
 return "kill_fail";
 }
 SecKillOrder order2 = orderService.getSecKillOrderByStudentIdAndCoursesId(student.getId(), coursesId);

 if (order2 != null) {
 return "kill_fail";
 }
 OrderInfo orderInfo = secKillService.secKill(student, course);
 */
        model.addAttribute("killMsg", CodeMsg.SECKILL_WAITTING.getMsg());
        model.addAttribute("user", student);
        model.addAttribute("coursesId", coursesId);
        return "kill_wait";
    }

    /**
     * 用于客户端轮询秒杀结果的接口
     *
     * @param model
     * @param
     * @param coursesId
     * @return -1：抢课失败 0-抢课排队中 orderId-抢课成功
     */
    @RequestMapping("/result")
    public String asyncSecKillResult(Model model, Student student, @RequestParam("coursesId") Long coursesId) {
        if (student == null) {
            return "kill_fail";
        }
        model.addAttribute("user", student);
        List<CoursesVO> coursesVO = coursesService.getCoursesDetailByStudentId(student.getId());
        model.addAttribute("coursesList", coursesVO);
        // 获取用户抢课结果
        Long status = secKillService.getSecKillResult(student.getId(), coursesId);
        String Msg;
        if (status == -1L) {
            Msg = "抢课失败";
        } else if (status == 0L) {
            Msg = "排队中";
        } else {
            Msg = "抢课成功";
        }
        model.addAttribute("status", Msg);
        return "kill_result";
    }

    /**
     * Spring提供的初始化后方法，在当前bean初始化后会被执行
     * 利用该方法在系统初始化时加载库存到Redis
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<CoursesVO> coursesVOS = coursesService.getCoursesVOs();
        if (coursesVOS != null && coursesVOS.size() > 0) {
            for (CoursesVO coursesVO : coursesVOS) {
                redisTemplate.opsForValue().set(CoursesKey.courseStockKey.getPrefix() + ":" + coursesVO.getId(),
                        coursesVO.getStockCount());
                coursesSecKillOverMap.put(coursesVO.getId(), false);
            }
        }
    }
}
