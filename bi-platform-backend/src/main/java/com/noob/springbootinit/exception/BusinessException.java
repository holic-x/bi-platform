package com.noob.springbootinit.exception;

import com.noob.springbootinit.common.ErrorCode;

/**
 * 自定义异常类
 * 结合ErrorCode使用，结合实际业务场景设定配置ErrorCode参数
 */
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public int getCode() {
        return code;
    }
}
