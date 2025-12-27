package com.my.auth.client;

import com.my.auth.dto.LoginReq;
import com.my.auth.dto.RegisterReq;
import com.my.common.model.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

import java.util.Map;

@HttpExchange("http://survivor/inner/survivor")
public interface SurvivorClient {

    @PostExchange("/verify")
    Mono<Result<Map<String, Object>>> verify(@RequestBody LoginReq req);

    @PostExchange("/register")
    Mono<Result<Map<String, Object>>> register(@RequestBody RegisterReq req);
}