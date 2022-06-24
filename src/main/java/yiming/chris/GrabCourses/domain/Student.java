package yiming.chris.GrabCourses.domain;

import lombok.Data;

import java.util.Date;


/**
 * ClassName:Student
 * Package:yiming.chris.GrabCourses.domain
 * Description:
 *
 * @Author: ChrisEli
 */
@Data
public class Student {
    private Long id;
    private String nickname;
    private String password;
    private String salt;
    private Date registerDate;
    private Date lastLoginDate;
    private Integer loginCount;
}
