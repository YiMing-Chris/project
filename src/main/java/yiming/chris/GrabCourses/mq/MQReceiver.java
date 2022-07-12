package yiming.chris.GrabCourses.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import yiming.chris.GrabCourses.config.RabbitMQConfig;
import yiming.chris.GrabCourses.domain.OrderInfo;
import yiming.chris.GrabCourses.domain.SecKillOrder;
import yiming.chris.GrabCourses.domain.Student;
import yiming.chris.GrabCourses.message.SecKillMessage;
import yiming.chris.GrabCourses.service.CoursesService;
import yiming.chris.GrabCourses.service.OrderService;
import yiming.chris.GrabCourses.service.SecKillService;
import yiming.chris.GrabCourses.vo.CoursesVO;

/**
 * ClassName:MQReceiver
 * Package:yiming.chris.GrabCourses.mq
 * Description:
 *
 * @Author: ChrisEli
 */
@Component
@RabbitListener(queues = RabbitMQConfig.QUEUE)
public class MQReceiver {

    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    @Autowired
    private CoursesService coursesService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SecKillService secKillService;


    @RabbitHandler
    public void receiveSecKillMessage1(@Payload SecKillMessage secKillMessage) {
        Student student = secKillMessage.getStudent();
        Long CourseId = secKillMessage.getCoursesId();

        logger.info("队列1收到用户" + student.getId() + "秒杀" + CourseId + "商品请求");

        // 判断容量
        CoursesVO course = coursesService.getCoursesDetailById(CourseId);
        if (course.getStockCount() <= 0) {
            return;
        }
        // 判断是否已经成功抢课，防止一人多次抢课成功
        SecKillOrder order = orderService.getSecKillOrderByStudentIdAndCoursesId(student.getId(), CourseId);
        if (order != null) {
            return;
        }

        // 正常进入秒杀流程：1.减少库存，2.创建订单，3-写入秒杀订单 三步需要原子操作
        OrderInfo orderInfo = secKillService.secKill(student, course);

    }
}
