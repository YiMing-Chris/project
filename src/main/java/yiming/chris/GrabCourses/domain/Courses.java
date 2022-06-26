package yiming.chris.GrabCourses.domain;

import lombok.Data;

/**
 * ClassName:Courses
 * Package:YiMing.chris.GrabCourses.domain
 * Description:
 *
 * @Author: ChrisEli
 */

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

    public void setId(Long id) {
        this.id = id;
    }

    public String getCoursesName() {
        return CoursesName;
    }

    public void setCoursesName(String coursesName) {
        CoursesName = coursesName;
    }

    public String getCoursesTitle() {
        return CoursesTitle;
    }

    public void setCoursesTitle(String coursesTitle) {
        CoursesTitle = coursesTitle;
    }

    public String getCoursesImg() {
        return CoursesImg;
    }

    public void setCoursesImg(String coursesImg) {
        CoursesImg = coursesImg;
    }

    public String getCoursesDetail() {
        return CoursesDetail;
    }

    public void setCoursesDetail(String coursesDetail) {
        CoursesDetail = coursesDetail;
    }

    public String getCoursesModule() {
        return CoursesModule;
    }

    public void setCoursesModule(String coursesModule) {
        CoursesModule = coursesModule;
    }

    public String getCoursesTeacher() {
        return CoursesTeacher;
    }

    public void setCoursesTeacher(String coursesTeacher) {
        CoursesTeacher = coursesTeacher;
    }

    public Integer getCoursesStock() {
        return CoursesStock;
    }

    public void setCoursesStock(Integer coursesStock) {
        CoursesStock = coursesStock;
    }

    public Integer getCoursesCapacity() {
        return CoursesCapacity;
    }

    public void setCoursesCapacity(Integer coursesCapacity) {
        CoursesCapacity = coursesCapacity;
    }
}
