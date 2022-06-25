package yiming.chris.GrabCourses.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.ibatis.javassist.tools.rmi.Sample;

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

    //密码测试
    public static void main(String[] args) {
        String salt = generate12ValidateSalt();
        System.out.println(userPasswordToDBPassword("password0", salt));
        System.out.println(salt);
    }

}
