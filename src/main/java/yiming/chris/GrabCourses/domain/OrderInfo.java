package yiming.chris.GrabCourses.domain;

import lombok.Data;

import java.util.Date;

/**
 * ClassName:OrderInfo
 * Package:yiming.chris.grabcourses.domain
 * Description:
 *
 * @Author: ChrisEli
 */
@Data
public class OrderInfo {
    private Long Id;
    private Long StudentId;
    private Long CoursesId;
    private String CoursesName;
    private Date CreateDate;
    private int Status;
}
