package yiming.chris.GrabCourses.dao;

import org.apache.ibatis.annotations.*;
import yiming.chris.GrabCourses.domain.OrderInfo;
import yiming.chris.GrabCourses.domain.SecKillOrder;

/**
 * ClassName:OrderDao
 * Package:yiming.chris.GrabCourses.dao
 * Description:
 *
 * @Author: ChrisEli
 */
@Mapper
public interface OrderDao {
    @Select("SELECT * FROM qiangke_order WHERE student_id = #{StudentId} AND courses_id = #{CoursesId}")
    SecKillOrder getSecKillOrderByStudentIdAndCoursesId(@Param("StudentId") Long StudentId, @Param("CoursesId") Long CoursesId);

    @Insert("INSERT INTO order_info VALUES (NULL, #{StudentId}, #{CoursesId}, #{CoursesName},#{CreateDate},#{Status})")
    @SelectKey(keyColumn = "id", keyProperty = "id", resultType = Long.class, before = false, statement = "SELECT last_insert_id()")
    Long insertOrderInfo(OrderInfo orderInfo);

    @Insert("INSERT INTO qiangke_order VALUES (NULL, #{StudentId}, #{OrderId}, #{CoursesId})")
    Long insertSecKillOrder(SecKillOrder secKillOrder);
}
