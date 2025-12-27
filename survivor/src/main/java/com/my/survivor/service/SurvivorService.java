package com.my.survivor.service;

import com.my.survivor.dto.rep.SurvivorRep;
import com.my.survivor.dto.req.ChangePasswordReq;
import com.my.survivor.dto.req.SurvivorCreateReq;
import com.my.survivor.dto.req.SurvivorUpdateReq;
import reactor.core.publisher.Mono;

public interface SurvivorService {

    /**
     * 创建新的幸存者档案 (登岛注册)
     */
    Mono<SurvivorRep> createSurvivor(SurvivorCreateReq req);

    Mono<SurvivorRep> getById(Long id);

    Mono<SurvivorRep> getByUserId(String userId);

    /**
     * 修改密码
     * @param userId 当前登录用户ID (从 header 获取)
     * @param req    包含旧密码和新密码
     */
    Mono<Void> changePassword(String userId, ChangePasswordReq req);

    Mono<Void> updateProfile(String userId, SurvivorUpdateReq req);

    // 校验用户密码，成功则返回用户信息（不含密码），失败抛异常
    Mono<SurvivorRep> verifyPassword(String userId, String password);
}