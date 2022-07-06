package yiming.chris.GrabCourses.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yiming.chris.GrabCourses.dao.OrderDao;
import yiming.chris.GrabCourses.domain.OrderInfo;
import yiming.chris.GrabCourses.domain.SecKillOrder;
import yiming.chris.GrabCourses.domain.Student;
import yiming.chris.GrabCourses.redis.OrderKey;
import yiming.chris.GrabCourses.vo.CoursesVO;

import java.util.Date;
import java.util.logging.Logger;

/**
 * ClassName:OrderService
 * Package:yiming.chris.GrabCourses.service
 * Description:
 * @Author: ChrisEli
 */
@Service
@Log4j2
public class OrderService {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取指定学生和指定课程的记录
     */
    public SecKillOrder getSecKillOrderByStudentIdAndCoursesId(Long StudentId, Long CoursesId) {
        // 先从缓存中查看，如果没有从数据库中查询
        SecKillOrder secKillOrder = getSecKillOrderByStudentIdAndCoursesIdFromCache(StudentId, CoursesId);
        if(secKillOrder != null ){
            log.info("从缓存中查看 {} 数据",StudentId);
            return secKillOrder ;
        }else{
            log.info("从数据库中查看 {} 数据",StudentId);
            return orderDao.getSecKillOrderByStudentIdAndCoursesId(StudentId, CoursesId);
        }
    }

    /**
     * 选课记录
     */
    @Transactional
    public OrderInfo createSecKillOrder(Student student, CoursesVO courses) {
        // 插入普通选课记录
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setCoursesId(courses.getId());
        orderInfo.setCoursesName(courses.getCoursesName());
        orderInfo.setCreateDate(new Date());
        orderInfo.setStatus(0);
        orderInfo.setStudentId(student.getId());
        orderDao.insertOrderInfo(orderInfo);

        // 插入抢课记录
        SecKillOrder secKillOrder = new SecKillOrder();
        secKillOrder.setCoursesId(courses.getId());
        secKillOrder.setOrderId(orderInfo.getId());
        secKillOrder.setStudentId(student.getId());
        orderDao.insertSecKillOrder(secKillOrder);

        // 在缓存中插入抢课记录
        insertSecKillOrderInCache(secKillOrder);

        return orderInfo;
    }

    /**
     * 从缓存中查询是否包含学生抢课记录
     */
    private SecKillOrder getSecKillOrderByStudentIdAndCoursesIdFromCache(Long studentId, Long CourseId) {
        return (SecKillOrder) redisTemplate.opsForValue().get(OrderKey.secKillOrderKey.getPrefix()
                + ":" + studentId + ":" + CourseId);
    }

    /**
     * 在缓存中插入抢课记录
     */
    private void insertSecKillOrderInCache(SecKillOrder secKillOrder) {
        redisTemplate.opsForValue().set(OrderKey.secKillOrderKey.getPrefix()
                + ":" + secKillOrder.getStudentId() + ":" + secKillOrder.getCoursesId(), secKillOrder);
    }
}
