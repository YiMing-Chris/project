package yiming.chris.GrabCourses.vo;

import lombok.Data;
import yiming.chris.GrabCourses.domain.Courses;

import java.util.Date;

/**
 * ClassName:CoursesVO
 * Package:yiming.chris.GrabCourses.vo
 * Description:
 *
 * @Author: ChrisEli
 */
@Data
public class CoursesVO extends Courses {
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

}
