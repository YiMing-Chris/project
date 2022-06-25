package yiming.chris.GrabCourses.context;

import yiming.chris.GrabCourses.domain.Student;

/**
 * ClassName:UserContext
 * Package:yiming.chris.GrabCourses.context
 * Description:
 *
 * @Author: ChrisEli
 */
public class UserContext {
    private static ThreadLocal<Student> userThreadLocal = new ThreadLocal<>();

    public static void setUser(Student student) {
        userThreadLocal.set(student);
    }

    public static Student getStudent() {
        return userThreadLocal.get();
    }
}