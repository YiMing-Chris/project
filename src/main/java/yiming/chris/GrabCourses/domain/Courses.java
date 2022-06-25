package yiming.chris.GrabCourses.domain;

import lombok.Data;

/**
 * ClassName:Courses
 * Package:YiMing.chris.GrabCourses.domain
 * Description:
 *
 * @Author: ChrisEli
 */
@Data
public class Courses {
    private Long id;
    private String CoursesName;
    private String CoursesTitle;
    private String CoursesImg;
    private String CoursesDetail;
    private String CoursesModule;
    private String CoursesTeacher;
    private Integer CoursesStock;
    private Integer CoursesCapacity;
    public Long getId(){
        return id;
    }
}
