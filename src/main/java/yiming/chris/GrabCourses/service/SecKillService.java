package yiming.chris.GrabCourses.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yiming.chris.GrabCourses.domain.OrderInfo;
import yiming.chris.GrabCourses.domain.SecKillOrder;
import yiming.chris.GrabCourses.domain.Student;
import yiming.chris.GrabCourses.redis.CoursesKey;
import yiming.chris.GrabCourses.vo.CoursesVO;

/**
 * ClassName:GoodsService
 * Package:yiming.chris.GrabCourses.service
 * Description:
 * @Author: ChrisEli
 */
@Service
public class SecKillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    private CoursesService coursesService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 减少容量，写入选课记录
     * 需要用事务保证原子性
     */
    @Transactional
    public OrderInfo secKill(Student student, CoursesVO course) {
        // 减少数据库课程的容量
        int updateRows = coursesService.reduceStock(course);
        logger.info("当前课程容量为：" + course.getStockCount() + "，减少容量更新记录数为：" + updateRows);
        // 减少容量成功才进行抢课
        if (updateRows == 1) {
            return orderService.createSecKillOrder(student, course);
        }
        // 如果容量没有更新成功，则不能进行抢课
        else {
            // 没有抢课成功，说明这门课程抢课结束了
            setSecKillOver(course.getId());
            return null;
        }
    }

    /**
     * 获取学生抢课结果
     * -1：抢课失败 0-抢课排队中 orderId-抢课成功
     */
    public Long getSecKillResult(Long studentId, Long courseId) {
        SecKillOrder order = orderService.getSecKillOrderByStudentIdAndCoursesId(studentId, courseId);
        if (order != null) {
            // 抢课成功
            return order.getOrderId();
        }
        // 还没有抢课成功，可能抢课失败，也可能还在排队，所以需要Redis设置抢课结束标记
        boolean isOver = getSecKillOver(courseId);
        return isOver ? -1L : 0L;
    }

    /**
     * 在Redis中设置某课程的抢课活动是否结束
     */
    private void setSecKillOver(Long courseId) {
        redisTemplate.opsForValue().set(CoursesKey.goodsSecKillOverKey.getPrefix() + ":" + courseId, true);
    }

    /**
     * 查询某课程的抢课活动是否结束
     * @param courseId
     * @return
     */
    private boolean getSecKillOver(Long courseId) {
        return redisTemplate.hasKey(CoursesKey.goodsSecKillOverKey.getPrefix() + ":" + courseId);
    }

}
