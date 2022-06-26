package yiming.chris.GrabCourses.vo;

import lombok.Data;
import yiming.chris.GrabCourses.domain.Courses;

import java.util.Date;

/**
 * ClassName:CoursesVO
 * Package:yiming.chris.GrabCourses.vo
 * Description:
 *
 * @Author: ChrisEli
 */

public class CoursesVO extends Courses {
    private Integer stockCount;
    private Date startDate;
    private Date endDate;

    public Integer getStockCount() {
        return stockCount;
    }

    public void setStockCount(Integer stockCount) {
        this.stockCount = stockCount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
