package com.my.common.exception;

import com.my.common.model.ResultCode;
import lombok.Getter;

/**
 * 自定义业务异常
 * 使用 RuntimeException 以便在 Lambda 表达式（WebFlux常用）中方便抛出
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = ResultCode.FAILURE.getCode();
    }

    public BusinessException(ResultCode resultCode) {
        super(resultCode.getMessage());
        this.code = resultCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}