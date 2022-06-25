package yiming.chris.GrabCourses.redis;

/**
 * ClassName:StudnetKey
 * Package:yiming.chris.GrabCourses.redis
 * Description:用户登录信息Session存储在Redis中
 * @Author: ChrisEli
 */
public class StudentKey extends BasePrefix{

    /**
     * Session过期时间 1天
     */
    private static final int TOKEN_EXPIRE = 3600 * 24 * 1;

    /**
     * 用户登录key
     */
    public static final StudentKey studentKey = new StudentKey(TOKEN_EXPIRE, "token");

    public StudentKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
