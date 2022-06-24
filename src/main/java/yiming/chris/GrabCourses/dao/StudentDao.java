package yiming.chris.GrabCourses.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import yiming.chris.GrabCourses.domain.Student;

/**
 * ClassName:StudentDao
 * Package:yiming.chris.GrabCourses.dao
 * Description:
 *
 * @Author: ChrisEli
 */
@Mapper
public interface StudentDao {

    @Select("SELECT * FROM qiangke_student WHERE id = #{id}")
    Student getUserById(@Param("id") Long id);


    @Insert("INSERT INTO qiangke_student VALUES (#{id}, #{nickname}, #{password}, #{salt}, #{registerDate}, #{lastLoginDate}, #{loginCount})")
    void insert(Student student);
}
