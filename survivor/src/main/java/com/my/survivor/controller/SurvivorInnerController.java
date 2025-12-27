package com.my.survivor.controller;

import com.my.common.model.Result;
import com.my.survivor.dto.rep.SurvivorRep;
import com.my.survivor.dto.req.SurvivorCreateReq;
import com.my.survivor.dto.req.UserVerifyReq;
import com.my.survivor.entity.Survivor;
import com.my.survivor.service.SurvivorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/inner/survivor")
@RequiredArgsConstructor
public class SurvivorInnerController {

    private final SurvivorService survivorService;

    /**
     * 内部接口：校验账号密码
     */
    @PostMapping("/verify")
    public Mono<Result<SurvivorRep>> verify(@RequestBody UserVerifyReq req) {
        return survivorService.verifyPassword(req.getUserId(), req.getPassword())
                .map(Result::success);
    }

    /**
     * 【第一步】注册幸存者档案 (登岛)
     * 每个人只能注册一次。
     * 成功后系统会下发一个身份 ID (如 "CN9527")，这是你在岛上的唯一凭证。
     * 请务必在手机端妥善保存该 ID。
     */
    @PostMapping("/register")
    public Mono<Result<SurvivorRep>> register(@RequestBody @Valid SurvivorCreateReq req) {
        return survivorService.createSurvivor(req)
                .map(Result::success);
    }
}