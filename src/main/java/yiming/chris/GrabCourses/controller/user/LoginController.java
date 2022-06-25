package yiming.chris.GrabCourses.controller.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import yiming.chris.GrabCourses.domain.Student;
import yiming.chris.GrabCourses.result.ServerResponse;
import yiming.chris.GrabCourses.service.StudentService;
import yiming.chris.GrabCourses.vo.LoginInfoVO;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * ClassName:LoginController
 * Package:yiming.chris.GrabCourses.controller.user
 * Description:
 * @Author: ChrisEli
 */
@Controller
@RequestMapping("/user")
public class LoginController {

    private Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private StudentService studentService;

    @RequestMapping("/login")
    public String login() {
        return "login";
    }

    @RequestMapping("/do_login")
    @ResponseBody
    public ServerResponse<String> doLogin(HttpServletResponse response, @Valid LoginInfoVO loginInfoVO) {
        // 打印用户输入信息日志
        logger.info(loginInfoVO.toString());
        // 登录，出错会抛出全局异常，并被捕获处理
        String token = studentService.login(response, loginInfoVO);
        // 直接返回true即可
        return ServerResponse.success(token);
    }


    /**
     * 根据Token获取用户信息
     */
    @RequestMapping("/info")
    @ResponseBody
    public ServerResponse<Student> userInfo(Student user) {
        return ServerResponse.success(user);
    }

}
