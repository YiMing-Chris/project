package yiming.chris.GrabCourses.controller.courses;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import yiming.chris.GrabCourses.annotation.AccessLimit;
import yiming.chris.GrabCourses.domain.Student;
import yiming.chris.GrabCourses.service.CoursesService;
import yiming.chris.GrabCourses.service.StudentService;
import yiming.chris.GrabCourses.vo.CoursesVO;

import java.util.List;

/**
 * ClassName:CoursesController
 * Package:yiming.chris.GrabCourses.controller.courses
 * Description:
 *
 * @Author: ChrisEli
 */
@Controller
@Slf4j
@RequestMapping("/courses")
public class CoursesController {
    @Autowired
    private StudentService studentService;
    @Autowired
    private CoursesService coursesService;

    /**
     * 展示课程列表
     *
     * @param model   将信息加入model用于页面展示
     * @param student 通过UserArgumentResolver解析参数token获取user对象
     * @return
     */
    @RequestMapping("/list")
    @AccessLimit(seconds = 10, maxValue = 5)
    public String goodsList(Model model, Student student) {
        model.addAttribute("user", student);
        List<CoursesVO> goods = coursesService.getCoursesVOs();
        model.addAttribute("coursesList", goods);
        log.info(student.getId() + "：查看了课程列表");
        return "courses_list";
    }

    /**
     * 展示商品详情页面
     *
     * @param model
     * @param student
     * @return
     */
    @RequestMapping("/detail/{coursesId}")
    public String goodsDetail(Model model, Student student, @PathVariable("coursesId") Long coursesId) {
        model.addAttribute("user", student);

        CoursesVO coursesVO = coursesService.getCoursesDetailById(coursesId);
        model.addAttribute("courses", coursesVO);

        Long startTime = coursesVO.getStartDate().getTime();
        Long endTime = coursesVO.getEndDate().getTime();
        Long now = System.currentTimeMillis();

        // 记录秒杀状态 0-未开始 1-正在进行 2-已结束
        int secKillStatus = 0;
        long remainSeconds = 0;

        // 秒杀未开始
        if (now < startTime) {
            secKillStatus = 0;
            remainSeconds = (startTime - now) / 1000;
        }
        // 秒杀已结束
        else if (now > endTime) {
            secKillStatus = 2;
            remainSeconds = -1;
        }
        // 秒杀正在进行
        else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("secKillStatus", secKillStatus);
        model.addAttribute("remainSeconds", remainSeconds);
        return "courses_detail";
    }
}
