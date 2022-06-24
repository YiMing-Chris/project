package yiming.chris.GrabCourses.message;

import yiming.chris.GrabCourses.domain.Student;

/**
 * ClassName:SecKillMessage
 * Package:yiming.chris.GrabCourses.message
 * Description:用户秒杀请求消息，用于MQ发送接收
 * @Author: ChrisEli
 */
public class SecKillMessage {
    private Student student;
    private Long CoursesId;

    public SecKillMessage() {
    }

    public SecKillMessage(Student student, Long CoursesId) {
        this.student = student;
        this.CoursesId = CoursesId;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Long getCoursesId() {
        return CoursesId;
    }

    public void setCoursesId(Long coursesId) {
        this.CoursesId = coursesId;
    }
}
