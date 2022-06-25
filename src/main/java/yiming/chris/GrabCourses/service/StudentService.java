package yiming.chris.GrabCourses.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import yiming.chris.GrabCourses.dao.StudentDao;
import yiming.chris.GrabCourses.domain.Student;
import yiming.chris.GrabCourses.exception.GlobalException;
import yiming.chris.GrabCourses.redis.StudentKey;
import yiming.chris.GrabCourses.result.CodeMsg;
import yiming.chris.GrabCourses.utils.MD5Util;
import yiming.chris.GrabCourses.utils.UUIDUtil;
import yiming.chris.GrabCourses.vo.LoginInfoVO;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static yiming.chris.GrabCourses.utils.MD5Util.userPasswordToDBPassword;
import static yiming.chris.GrabCourses.utils.ValidateSaltUtil.generate12ValidateSalt;

/**
 * ClassName:StudentService
 * Package:yiming.chris.GrabCourses.service
 * Description:
 *
 * @Author: ChrisEli
 */
@Service
public class StudentService {
    public static final String COOKIE_TOKEN_NAME = "TOKEN";

    @Autowired
    private StudentDao studentDao;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    /**
     * 登录服务，包含第二次MD5加密的逻辑
     *
     * @param loginInfoVO
     * @return
     */
    public String login(HttpServletResponse response, LoginInfoVO loginInfoVO) {
        if (loginInfoVO == null) {
            throw new GlobalException(CodeMsg.SERVER_ERROR);
        }

        String mobile = loginInfoVO.getId();
        String formPassword = loginInfoVO.getPassword();

        Student student = studentDao.getStudentById(Long.parseLong(mobile));
        if (student == null) {
            throw new GlobalException(CodeMsg.StudentId_NOT_EXIST);
        }

        // 验证密码，二次MD5加密验证
        String salt = student.getSalt();
        String dbPassword = MD5Util.formPasswordToDBPassword(formPassword, salt);
        if (!student.getPassword().equals(dbPassword)) {
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }

        // 用户输入信息正确后，在Redis中保存Session信息，浏览器保存Cookie
        String token = addCookie(response, student);

        return token;
    }

    /**
     * 在Redis中保存Session信息，浏览器保存Cookie，返回Token
     *
     * @param response
     * @param student
     */
    private String addCookie(HttpServletResponse response, Student student) {
        String token = UUIDUtil.uuid();
        String key = StudentKey.studentKey.getPrefix() + ":" + token;
        int expire = StudentKey.studentKey.expireSeconds();
        redisTemplate.opsForValue().set(key, student);
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);

        Cookie cookie = new Cookie(COOKIE_TOKEN_NAME, token);
        cookie.setMaxAge(expire);
        cookie.setPath("/");
        response.addCookie(cookie);
        return token;
    }

    /**
     * 批量注册用户功能，用于JMeter的压测
     *
     * @param num 批量数量
     * @return
     */

    public CodeMsg batchRegister(Integer num) {
        if (num < 1) {
            throw new GlobalException(CodeMsg.REGISTER_BATCH);
        }
        for (int i = 0; i < num; i++) {
            Student student = new Student();
            student.setId(12320890318L + i);
            student.setNickname("student" + i);
            student.setPassword(userPasswordToDBPassword("password" + i, generate12ValidateSalt()));
            student.setSalt(generate12ValidateSalt());
            student.setRegisterDate(new Date());
            student.setLastLoginDate(new Date());
            student.setLoginCount(0);
            studentDao.insert(student);
        }
        return CodeMsg.SUCCESS;
    }

    /**
     * 根据Token查询用户信息
     */
    public Student getUserByToken(String token) {
        //如果token为空，直接返回
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        String key = StudentKey.studentKey.getPrefix() + ":" + token;
        int expire = StudentKey.studentKey.expireSeconds();
        // 更新用户Session有效期，如果key不存在，并不会在redis新生成key
        redisTemplate.expire(key, expire, TimeUnit.SECONDS);
        return (Student) redisTemplate.opsForValue().get(key);
    }

}
