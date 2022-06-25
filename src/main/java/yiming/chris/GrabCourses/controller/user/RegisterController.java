package yiming.chris.GrabCourses.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import yiming.chris.GrabCourses.result.ServerResponse;
import yiming.chris.GrabCourses.service.StudentService;

/**
 * ClassName:RegisterController
 * Package:yiming.chris.GrabCourses.controller.user
 * Description:用户注册
 * @Author: ChrisEli
 */
@Controller
@RequestMapping("/user")
public class RegisterController {
    @Autowired
    private StudentService studentService;

    @RequestMapping("/batch_register/{num}")
    @ResponseBody
    public ServerResponse<Boolean> batchRegister(@PathVariable("num") Integer num) {
        studentService.batchRegister(num);
        return ServerResponse.success(true);
    }
}
