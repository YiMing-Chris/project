package yiming.chris.GrabCourses.vo;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

/**
 * ClassName:LoginInfoVO
 * Package:yiming.chris.GrabCourses.vo
 * Description:
 *
 * @Date:2022/6/25 2:05
 * @Author: ChrisEli
 */
@Data
public class LoginInfoVO {
    @NotNull
    @Length(min = 8, max = 8)
    private String id;
    @NotNull
    @Length(min = 32, max = 32)
    private String password;


    @Override
    public String toString() {
        return "LoginInfoVO{" +
                "id='" + id + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
