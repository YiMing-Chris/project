package yiming.chris.GrabCourses.domain;

import lombok.Data;

import java.util.Date;

/**
 * ClassName:SecKillCourses
 * Package:yiming.chris.grabcourses.domain
 * Description:
 *
 * @Author: ChrisEli
 */
@Data
public class SecKillCourses {
    private Long id;
    private Long CoursesId;
    private Integer StockCount;
    private Date StartDate;
    private Date EndDate;

}
