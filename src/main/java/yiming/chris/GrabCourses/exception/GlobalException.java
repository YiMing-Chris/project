package yiming.chris.GrabCourses.exception;

import yiming.chris.GrabCourses.result.CodeMsg;

/**
 * ClassName:GlobalException
 * Package:yiming.chris.GrabCourses.exception
 * Description:
 *
 * @Author: ChrisEli
 */
public class GlobalException extends RuntimeException {

    private CodeMsg codeMsg;

    public GlobalException(CodeMsg codeMsg) {
        this.codeMsg = codeMsg;
    }

    public CodeMsg getCodeMsg() {
        return codeMsg;
    }
}
