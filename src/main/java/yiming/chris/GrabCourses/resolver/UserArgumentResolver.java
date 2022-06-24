package yiming.chris.GrabCourses.resolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import yiming.chris.GrabCourses.context.UserContext;
import yiming.chris.GrabCourses.domain.Student;
import yiming.chris.GrabCourses.service.StudentService;

/**
 * ClassName:UserArgumentResolver
 * Package:yiming.chris.GrabCourses.resolver
 * Description:
 *
 * @Author: ChrisEli
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {


    /**
     * 判断是否支持对应的参数类型，即参数是否是User类型
     * @param methodParameter 方法参数
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();
        return parameterType == Student.class;
    }

    /**
     * 解析参数，封装返回User对象
     * @param methodParameter 方法参数
     * @param modelAndViewContainer
     * @param nativeWebRequest
     * @param webDataBinderFactory
     * @return
     * @throws Exception
     */
    @Nullable
    @Override
    public Object resolveArgument(MethodParameter methodParameter, @Nullable ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, @Nullable WebDataBinderFactory webDataBinderFactory) throws Exception {
        return UserContext.getStudent();
    }
}
