package com.my.auth.service;

import com.my.auth.dto.LoginReq;
import com.my.auth.dto.RegisterReq;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface AuthService {
    /**
     * 登录
     */
    Mono<Map<String, Object>> login(LoginReq req);

    /**
     * 注册
     */
    Mono<Map<String, Object>> register(RegisterReq req);
}