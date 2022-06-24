package yiming.chris.GrabCourses.annotation;

import java.lang.annotation.*;

/**
 * ClassName:AccessLimit
 * Package:yiming.chris.GrabCourses.annotation
 * Description:
 *
 * @Author: ChrisEli
 */
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Target(ElementType.METHOD)
    public @interface AccessLimit {

        /**
         * 指定时间
         * @return
         */
        int seconds() default 60;

        /**
         * seconds时间内最多允许访问maxValue次数
         * @return
         */
        int maxValue();

        /**
         * 该请求是否需要登录
         * @return
         */
        boolean needLogin() default true;
}
