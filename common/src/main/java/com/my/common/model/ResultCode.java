package com.my.common.model;

import lombok.Getter;

/**
 * 统一 API 响应码
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILURE(500, "业务异常"),
    
    // 认证授权相关
    UNAUTHORIZED(401, "未登录或Token失效"),
    FORBIDDEN(403, "无权限访问"),
    
    // 参数错误
    PARAM_ERROR(400, "参数校验失败"),
    // 参数缺失 (漏传必填项)
    PARAM_MISSING(40001, "缺少必要的请求参数"),
    
    // 系统相关
    SYSTEM_ERROR(500, "系统繁忙，请稍后再试");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}