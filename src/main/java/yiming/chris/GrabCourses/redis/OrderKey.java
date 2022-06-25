package yiming.chris.GrabCourses.redis;

/**
 * ClassName:OrderKey
 * Package:yiming.chris.GrabCourses.redis
 * Description:订单存储Redis的Key
 * @Author: ChrisEli
 */
public class OrderKey extends BasePrefix{
    //抢课结果Key设置TTL为一天
    private static final int SECKILL_ORDER_EXPIRE = 3600 * 24 * 1;

    public static OrderKey secKillOrderKey = new OrderKey(SECKILL_ORDER_EXPIRE, "secKillOrder");

    public OrderKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }
}
