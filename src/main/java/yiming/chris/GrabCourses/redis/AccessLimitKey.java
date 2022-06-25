package yiming.chris.GrabCourses.redis;

/**
 * ClassName:AccessLimitKey
 * Package:yiming.chris.GrabCourses.redis
 * Description:
 * @Author: ChrisEli
 */
public class AccessLimitKey extends BasePrefix{
    /**
     * 接口访问限流次数key
     */
    private static final String ACCESS_PREFIX = "access_limit_key";

    public static AccessLimitKey getAccessKeyWithExpire(int expireSeconds) {
        return new AccessLimitKey(expireSeconds, ACCESS_PREFIX);
    }

    private AccessLimitKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
