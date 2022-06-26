package yiming.chris.GrabCourses.redis;

/**
 * ClassName:CoursesKey
 * Package:yiming.chris.GrabCourses.redis
 * Description:课程的Redis存储Key
 * @Author: ChrisEli
 */
public class CoursesKey extends BasePrefix{
    /**
     * 课程容量key
     */
    public static final CoursesKey courseStockKey = new CoursesKey(0, "coursesStock");
    /**
     * 抢课活动是否结束key
     */
    public static final CoursesKey coursesSecKillOverKey = new CoursesKey(0, "coursesSecKillIsOver");

    public CoursesKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
