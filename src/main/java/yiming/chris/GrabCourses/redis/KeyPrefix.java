package yiming.chris.GrabCourses.redis;

/**
 * ClassName:KeyPrefix
 * Package:yiming.chris.GrabCourses.redis
 * Description:运用模板设计模式
 * @Author: ChrisEli
 */
public interface KeyPrefix {
    /**
     * 获取过期时间
     */
    int expireSeconds();

    /**
     * 获取key的前缀，用于标识不同的业务的key
     */
    String getPrefix();
}
