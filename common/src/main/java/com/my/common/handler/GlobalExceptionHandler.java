package com.my.common.handler; // 注意包名变化

import com.my.common.exception.BusinessException;
import com.my.common.model.Result;
import com.my.common.model.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

// ... 类内容和之前一样 ...
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public Mono<Result<Void>> handleBusinessException(BusinessException e) {
        log.warn("捕获业务异常: {}", e.getMessage());
        return Mono.just(Result.fail(e.getCode(), e.getMessage()));
    }

    /**
     * 处理参数校验异常
     * 当 @Valid 校验失败时，WebFlux 会抛出 WebExchangeBindException
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<Result<Void>> handleBindException(WebExchangeBindException e) {
        // 将所有的错误信息拼接成一个字符串，或者只取第一个
        String errorMsg = e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(", "));

        log.warn("参数校验失败: {}", errorMsg);
        // 使用 ResultCode.PARAM_ERROR (400)
        return Mono.just(Result.fail(ResultCode.PARAM_ERROR.getCode(), "参数校验失败: " + errorMsg));
    }

    /**
     * 【新增】处理请求参数缺失异常 (WebFlux)
     * 比如 @RequestParam, @RequestHeader, @PathVariable 必填项缺失时抛出
     */
    @ExceptionHandler(ServerWebInputException.class)
    public Mono<Result<Void>> handleServerWebInputException(ServerWebInputException e) {
        // e.getReason() 通常会包含 "Missing request header 'X-User-Id'..." 这样清晰的信息
        log.warn("请求参数缺失: {}", e.getReason());
        return Mono.just(Result.fail(ResultCode.PARAM_MISSING.getCode(), "请求参数缺失: " + e.getReason()));
    }

    @ExceptionHandler(Exception.class)
    public Mono<Result<Void>> handleException(Exception e) {
        log.error("捕获系统异常", e);
        return Mono.just(Result.fail("系统繁忙，请稍后重试"));
    }


}