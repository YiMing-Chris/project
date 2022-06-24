package yiming.chris.GrabCourses.utils;

import java.util.UUID;

/**
 * ClassName:UUIDUtil
 * Package:yiming.chris.GrabCourses.utils
 * Description:
 *
 * @Author: ChrisEli
 */
public class UUIDUtil {
    /**
     * 产生UUID字符串，将“-”去除
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
