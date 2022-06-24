package yiming.chris.GrabCourses.domain;

import lombok.Data;

/**
 * ClassName:SecKillOrder
 * Package:yiming.chris.GrabCourses.domain
 * Description:
 *
 * @Author: ChrisEli
 */
@Data
public class SecKillOrder {
    private Long Id;
    private Long StudentId;
    private Long  OrderId;
    private Long CoursesId;
}
