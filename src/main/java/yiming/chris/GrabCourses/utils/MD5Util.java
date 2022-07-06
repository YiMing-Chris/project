package yiming.chris.GrabCourses.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.javassist.tools.rmi.Sample;
import yiming.chris.GrabCourses.domain.Student;

import javax.servlet.ServletOutputStream;

import java.nio.charset.StandardCharsets;

import static yiming.chris.GrabCourses.utils.ValidateSaltUtil.generate12ValidateSalt;

/**
 * ClassName:MD5Util
 * Package:yiming.chris.GrabCourses.utils
 * Description:
 *
 * @Author: ChrisEli
 */
public class MD5Util {
    /**
     * 客户端固定的盐值
     */
    private static final String SALT = "1a2b3c4d";

    /**
     * MD5加密
     * @param source
     * @return
     */
    private static String MD5(String source) {
        return DigestUtils.md5Hex(source);
    }

    /**
     * 用户明文密码进行MD5加密
     * @param password 明文密码
     * @return MD5加密后的密码
     */
    public static String userPasswordToFormPassowrd(String password) {
        return MD5(SALT.substring(0, SALT.length() / 2)
                + password + SALT.substring(SALT.length() / 2));
    }

    /**
     * 表单密码进行MD5加密保存至数据库
     * @param formPassword 表单密码
     * @param randomSalt 随机盐值
     * @return
     */
    public static String formPasswordToDBPassword(String formPassword, String randomSalt) {
        return MD5(randomSalt.substring(0, randomSalt.length() / 2)
                + formPassword + SALT.substring(randomSalt.length() / 2));
    }

    /**
     * 用户明文密码两次MD5加密
     * @param userPassword
     * @param randomSalt
     * @return
     */
    public static String userPasswordToDBPassword(String userPassword, String randomSalt) {
        return formPasswordToDBPassword(userPasswordToFormPassowrd(userPassword), randomSalt);
    }

    /**
     * 字符串大小计算器
     * @param text
     */
    public static long getStringSize(String text) {
        byte[] textlength = text.getBytes(StandardCharsets.UTF_8); //得到占多少 bytes

        long size = textlength.length/1024; //得到字符串占多少 KB

        return size;

    }
    //密码测试
    public static void main(String[] args) {
//        String salt = generate12ValidateSalt();
//        System.out.println(userPasswordToDBPassword("password0", salt));
//        System.out.println(salt);
        String text = "  \"@class\": \"yiming.chris.GrabCourses.domain.Student\",\n" +
                "  \"id\": 19195102,\n" +
                "  \"nickname\": \"student1\",\n" +
                "  \"password\": \"37b7e8fdb1d6881344bfc3c015ef6819\",\n" +
                "  \"salt\": \"NOGPWGOmsgcw\",\n" +
                "  \"registerDate\": [\n" +
                "    \"java.util.Date\",\n" +
                "    1656180854000\n" +
                "  ],\n" +
                "  \"lastLoginDate\": [\n" +
                "    \"java.util.Date\",\n" +
                "    1656180854000\n" +
                "  ],\n" +
                "  \"loginCount\": 0 ";
//        System.out.println(getStringSize(text));
        for(int i=0;i < 10000 ;i++) {
            System.out.print(19195101L + i);
            System.out.println(","+userPasswordToFormPassowrd("password" + i));
        }
    }
    

}
