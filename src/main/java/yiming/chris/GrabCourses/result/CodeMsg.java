package yiming.chris.GrabCourses.result;

/**
 * ClassName:CodeMsg
 * Package:yiming.chris.GrabCourses.result
 * Description:
 *
 * @Author: ChrisEli
 */
public class CodeMsg {

    /**
     * 通用状态码
     */
    public static final CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static final CodeMsg SERVER_ERROR = new CodeMsg(500100,"服务端异常");
    public static final CodeMsg BIND_ERROR = new CodeMsg(500101,"参数校验异常: %s");
    public static final CodeMsg ACCESS_BEYOND_LIMIT = new CodeMsg(500102, "访问过于频繁");

    /**
     * 用户模块5002XX
     */
    public static final CodeMsg SESSION_ERROR = new CodeMsg(500210,"Session不存在或已经失效");
    public static final CodeMsg PASSWORD_EMPTY = new CodeMsg(500211,"密码不能为空");
    public static final CodeMsg StudentId_EMPTY = new CodeMsg(500212,"学号不能为空");
    public static final CodeMsg StudentId_ERROR = new CodeMsg(500213, "学号格式错误");
    public static final CodeMsg StudentId_NOT_EXIST = new CodeMsg(500214, "学号不存在");
    public static final CodeMsg PASSWORD_ERROR = new CodeMsg(500215, "密码错误");
    public static final CodeMsg REGISTER_BATCH = new CodeMsg(500216, "批量注册数量设置错误");

    /**
     * 课程模块5003XX
     */
    public static final CodeMsg PRODUCT_ERROR = new CodeMsg(500301,"服务端异常");

    /**
     * 抢课结果模块5004XX
     */
    public static final CodeMsg ORDER_ERROR = new CodeMsg(500401,"服务端异常");

    /**
     * 抢课模块5005XX
     */
    public static final CodeMsg SECKILL_OVER = new CodeMsg(500501,"抢课结束");
    public static final CodeMsg SECKILL_REPEAT = new CodeMsg(500502, "不能重复抢课");
    public static final CodeMsg SECKILL_WAITTING = new CodeMsg(500503, "抢课排队中");

    private int code;
    private String msg;

    public CodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }

    /**
     * 用于参数校验错误信息的拼接
     * @param args 错误信息拼接
     * @return
     */
    public CodeMsg fillMsg(Object... args) {
        msg = String.format(msg, args);
        return this;
    }
}
