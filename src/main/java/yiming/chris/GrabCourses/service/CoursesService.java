package yiming.chris.GrabCourses.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import yiming.chris.GrabCourses.dao.CoursesDao;
import yiming.chris.GrabCourses.domain.SecKillCourses;
import yiming.chris.GrabCourses.vo.CoursesVO;

import java.util.List;

/**
 * ClassName:CoursesService
 * Package:yiming.chris.GrabCourses.service
 * Description:
 * @Author: ChrisEli
 */
@Service
public class CoursesService {

    @Autowired
    private CoursesDao coursesDao;

    /**
     * 获取所有抢课课程详情
     */
    public List<CoursesVO> getCoursesVOs() {
        return coursesDao.getCoursesVOs();
    }

    /**
     * 通过学号获取所有抢课课程详情
     */
    public CoursesVO getCoursesVO(Long StudentId) {
        return coursesDao.getCoursesVO(StudentId);
    }

    /**
     * 获取某门课程详细信息
     */
    public CoursesVO getCoursesDetailById(Long courseId) {
        return coursesDao.getCoursesDetailById(courseId);
    }

    /**
     * 减小容量，这里不需要更新库存，库存的正确更改依靠SQL实现
     */
    public int reduceStock(CoursesVO courses) {
        SecKillCourses c = new SecKillCourses();
        c.setCoursesId(courses.getId());
        return coursesDao.updateStock(c);
    }
}
