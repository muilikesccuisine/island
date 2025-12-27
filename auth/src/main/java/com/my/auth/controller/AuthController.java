package com.my.auth.controller;

import com.my.auth.dto.LoginReq;
import com.my.auth.dto.RegisterReq;
import com.my.auth.service.AuthService;
import com.my.common.model.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Mono<Result<Map<String, Object>>> login(@RequestBody LoginReq req) {
        return authService.login(req)
                .map(Result::success);
    }

    @PostMapping("/register")
    public Mono<Result<Map<String, Object>>> register(@RequestBody RegisterReq req) {
        return authService.register(req)
                .map(Result::success);
    }
}