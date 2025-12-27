package com.my.survivor.controller;

import com.my.common.model.Result;
import com.my.survivor.dto.rep.SurvivorRep;
import com.my.survivor.dto.req.ChangePasswordReq;
import com.my.survivor.dto.req.SurvivorUpdateReq;
import com.my.survivor.service.SurvivorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/survivor")
@RequiredArgsConstructor
public class SurvivorController {

    private final SurvivorService survivorService;

    /**
     * 查看自己的档案
     * 用于确认自己的健康状态、贡献点余额等。
     * 需要在 HTTP Header 中携带 "X-User-Id"
     */
    @GetMapping("/my")
    public Mono<Result<SurvivorRep>> getMyProfile(@RequestHeader("X-User-Id") String userId) {
        return survivorService.getByUserId(userId)
                .map(Result::success);
    }

    /**
     * 查看特定幸存者的档案
     * 用于在岛上遇到陌生人时确认对方身份，或者管理员查看成员信息。
     * @param id 幸存者的数据库主键 ID (不是 CN 编号，虽然通常大家会互报 CN 编号，但系统内部交互还是推荐用 Long ID)
     *           注意：如果需要通过 CN 编号查找，我们可以再加一个接口
     */
    @GetMapping("/{id}")
    public Mono<Result<SurvivorRep>> getById(@PathVariable Long id) {
        return survivorService.getById(id)
                .map(Result::success);
    }

    /**
     * 修改密码
     */
    @PostMapping("/password/change")
    public Mono<Result<Void>> changePassword(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid ChangePasswordReq req) {
        return survivorService.changePassword(userId, req)
                .thenReturn(Result.success()); // 执行成功后返回统一成功结果
    }

    /**
     * 修改个人资料 (头像等)
     * 注意：关键档案信息(姓名、年龄、身体状况)不可自行修改。
     */
    @PostMapping("/profile/update")
    public Mono<Result<Void>> updateProfile(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody SurvivorUpdateReq req) {
        return survivorService.updateProfile(userId, req)
                .thenReturn(Result.success());
    }
}