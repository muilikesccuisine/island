package com.my.auth.service.impl;

import com.my.auth.client.SurvivorClient;
import com.my.auth.dto.LoginReq;
import com.my.auth.dto.RegisterReq;
import com.my.auth.service.AuthService;
import com.my.common.constant.SurvivorConstants;
import com.my.common.exception.BusinessException;
import com.my.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SurvivorClient survivorClient; // 注入声明式客户端
    private final JwtUtil jwtUtil;

    // 不需要再注入 WebClient 和 survivorServiceUrl 了

    @Override
    public Mono<Map<String, Object>> login(LoginReq req) {
        return survivorClient.verify(req)
                .flatMap(result -> {
                    if (result.getCode() != 200 || result.getData() == null) {
                        // 注意：Result.msg 字段名需确认是 msg 还是 message
                        String msg = result.getMsg() != null ? result.getMsg() : "登录失败";
                        return Mono.error(new BusinessException(msg));
                    }
                    return buildLoginSuccessResponse(result.getData());
                })
                .onErrorResume(e -> handleServiceError("登录", e));
    }

    @Override
    public Mono<Map<String, Object>> register(RegisterReq req) {
        return survivorClient.register(req)
                .flatMap(result -> {
                    if (result.getCode() != 200) {
                        return Mono.error(new BusinessException(result.getMsg()));
                    }
                    return buildLoginSuccessResponse(result.getData());
                })
                .onErrorResume(e -> handleServiceError("注册", e));
    }

    private Mono<Map<String, Object>> handleServiceError(String action, Throwable e) {
        log.error("{}异常", action, e);
        if (e instanceof BusinessException) {
            return Mono.error(e);
        }
        return Mono.error(new BusinessException(action + "服务暂不可用: " + e.getMessage()));
    }

    private Mono<Map<String, Object>> buildLoginSuccessResponse(Map<String, Object> survivorData) {
        String userId = String.valueOf(survivorData.get(SurvivorConstants.USERID));
        String name = String.valueOf(survivorData.get(SurvivorConstants.NAME));
        String dbId = String.valueOf(survivorData.get(SurvivorConstants.ID));
        String avatarUrl = String.valueOf(survivorData.get(SurvivorConstants.AVATARURL));
        Integer islandGrade = (Integer) survivorData.get(SurvivorConstants.ISLANDGRADE);

        String token = jwtUtil.generateToken(dbId, name);

        return Mono.just(Map.of(
                SurvivorConstants.TOKEN, token,
                SurvivorConstants.SURVIVOR, Map.of(
                        SurvivorConstants.ID, dbId,
                        SurvivorConstants.USERID, userId,
                        SurvivorConstants.NAME, name,
                        SurvivorConstants.AVATARURL, avatarUrl != null ? avatarUrl : "",
                        SurvivorConstants.ISLANDGRADE, islandGrade
                )
        ));
    }
}