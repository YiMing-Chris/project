package yiming.chris.GrabCourses.redis;

import yiming.chris.GrabCourses.domain.Courses;

/**
 * ClassName:CoursesKey
 * Package:yiming.chris.GrabCourses.redis
 * Description:课程的Redis存储Key
 * @Author: ChrisEli
 */
public class CoursesKey extends BasePrefix{
    /**
     * 商品库存key
     */
    public static final CoursesKey courseStockKey = new CoursesKey(0, "coursesStock");
    /**
     * 商品秒杀活动是否结束key
     */
    public static final CoursesKey goodsSecKillOverKey = new CoursesKey(0, "coursesSecKillIsOver");

    public CoursesKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
