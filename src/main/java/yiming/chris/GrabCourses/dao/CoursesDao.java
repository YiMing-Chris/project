package yiming.chris.GrabCourses.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import yiming.chris.GrabCourses.domain.SecKillCourses;
import yiming.chris.GrabCourses.vo.CoursesVO;

import java.util.List;

/**
 * ClassName:CoursesDao
 * Package:yiming.chris.GrabCourses.dao
 * Description:
 *
 * @Author: ChrisEli
 */
@Mapper
public interface CoursesDao {

    //获取所有抢课课程详情
    @Select("SELECT c.*,  qc.stock_count, qc.start_date, qc.end_date FROM courses c RIGHT JOIN qiangke_courses qc ON c.id = qc.courses_id")
    List<CoursesVO> getCoursesVOs();

    //通过学号获取抢课课程详情
    @Select("SELECT c.* FROM courses c LEFT JOIN qiangke_order qo ON c.id = qo.courses_id and qo.student_id =#{StudentId} ")
    CoursesVO getCoursesVO(Long StudentId);


    @Select("SELECT c.*,  qc.stock_count, qc.start_date, qc.end_date FROM courses c INNER JOIN qiangke_courses qc ON c.id = qc.courses_id AND c.id = #{CoursesId}")
    CoursesVO getCoursesDetailById(@Param("CoursesId") Long CoursesId);


    @Update("UPDATE qiangke_courses SET stock_count = stock_count - 1 WHERE courses_id = #{CoursesId} AND stock_count > 0")
    int updateStock(SecKillCourses g);
}
