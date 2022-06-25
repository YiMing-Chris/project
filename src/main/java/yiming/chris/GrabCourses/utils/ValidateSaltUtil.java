package yiming.chris.GrabCourses.utils;

import java.util.Random;

/**
 * ClassName:ValidateSaltUtil
 * Package:yiming.chris.GrabCourses.utils
 * Description:生成12位随机盐
 * @Date:2022/6/25 20:34
 * @Author: ChrisEli
 */
public class ValidateSaltUtil {
    public static String generate12ValidateSalt() {
        String str = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            char ch = str.charAt(new Random().nextInt(str.length()));
            sb.append(ch);
        }
        return sb.toString();
    }
}
