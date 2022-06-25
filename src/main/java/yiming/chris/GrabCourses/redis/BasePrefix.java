package yiming.chris.GrabCourses.redis;

/**
 * ClassName:BasePrefix
 * Package:yiming.chris.GrabCourses.redis
 * Description:基础KeyPrefix的实现
 * @Author: ChrisEli
 */
public class BasePrefix implements KeyPrefix{
    private int expireSeconds;
    private String prefix;

    //构造器
    public BasePrefix(int expireSeconds, String prefix) {
        this.expireSeconds = expireSeconds;
        this.prefix = prefix;
    }

    public BasePrefix(String prefix) {
        // 过期时间默认0表示不过期
        this(0, prefix);
    }

    @Override
    public int expireSeconds() {
        return expireSeconds;
    }

    @Override
    public String getPrefix() {
        String className = getClass().getSimpleName();
        return className + ":" + prefix;
    }
}
