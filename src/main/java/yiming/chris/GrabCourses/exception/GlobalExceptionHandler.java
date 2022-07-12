package yiming.chris.GrabCourses.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import yiming.chris.GrabCourses.result.CodeMsg;
import yiming.chris.GrabCourses.result.ServerResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * ClassName:GlobalExceptionHandler
 * Package:yiming.chris.GrabCourses.exception
 * Description:
 *
 * @Author: ChrisEli
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ServerResponse<CodeMsg> exceptionHandler(HttpServletRequest request, Exception e) {
        // 打印log
        logger.error(e.getMessage());
        logger.error(String.valueOf(e));
        e.printStackTrace();

        // 参数校验异常
        if (e instanceof BindException) {
            BindException bindException = (BindException) e;
            List<ObjectError> errors = bindException.getAllErrors();
            String msg = errors.get(0).getDefaultMessage();
            return ServerResponse.error(CodeMsg.BIND_ERROR.fillMsg(msg));
        }
        // 全局异常
        else if (e instanceof GlobalException) {
            GlobalException globalException = (GlobalException) e;
            return ServerResponse.error(globalException.getCodeMsg());
        } else {
            return ServerResponse.error(CodeMsg.SERVER_ERROR);
        }
    }

}
