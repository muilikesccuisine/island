package com.my.governance.service.impl;

import com.my.common.exception.BusinessException;
import com.my.common.util.IdUtils;
import com.my.governance.client.SurvivorClient;
import com.my.governance.dto.req.AdminReq;
import com.my.governance.dto.req.ChangeGradeReq;
import com.my.governance.entity.GovernanceMember;
import com.my.governance.repository.GovernanceMemberRepository;
import com.my.governance.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final GovernanceMemberRepository memberRepository;
    private final SurvivorClient survivorClient;

    @Override
    @Transactional
    public Mono<Void> appointAdmin(AdminReq req) {
        // 1. 校验操作人是否为岛主
        return memberRepository.findBySurvivorIdAndStatus(req.getOperatorId(), 1)
                .filter(m -> Objects.equals(m.getRole(), 2))
                .switchIfEmpty(Mono.error(new BusinessException("只有岛主有权任命管理员")))
                .flatMap(owner ->
                        // 2. 比例校验
                        Mono.zip(survivorClient.getSurvivorCount(), memberRepository.findAllByStatus(1).count())
                                .flatMap(tuple -> {
                                    int totalSurvivors = tuple.getT1();
                                    long currentAdminsCount = tuple.getT2();

                                    // 向下取整判断：(当前总数 + 1) 不能超过 (总人数 / 10)
                                    if ((currentAdminsCount + 1) > (totalSurvivors / 10)) {
                                        return Mono.error(new BusinessException("管理员名额已满 (当前上限: " + (totalSurvivors / 10) + ")"));
                                    }

                                    // 3. 执行任命
                                    GovernanceMember admin = new GovernanceMember();
                                    admin.setId(IdUtils.getSnowflakeId());
                                    admin.markAsNew();
                                    admin.setSurvivorId(req.getSurvivorId());
                                    admin.setRole(1);
                                    admin.setStatus(1);
                                    admin.setAppointedBy(req.getOperatorId());
                                    admin.setAppointTime(LocalDateTime.now());

                                    ChangeGradeReq changeGradeReq = new ChangeGradeReq();
                                    changeGradeReq.setSurvivorId(req.getSurvivorId());
                                    changeGradeReq.setGrade(1);

                                    return memberRepository.save(admin)
                                            .then(survivorClient.updateSurvivorGrade(changeGradeReq)); // 1 为 ADMIN
                                })
                ).then();
    }

    @Override
    @Transactional
    public Mono<Void> dismissAdmin(AdminReq req) {
        return memberRepository.findBySurvivorIdAndStatus(req.getSurvivorId(), 1)
                .filter(m -> Objects.equals(m.getRole(), 1))
                .switchIfEmpty(Mono.error(new BusinessException("该成员当前不是在职管理员")))
                .flatMap(member -> {
                    // 校验：要么是岛主撤职，要么是管理员自己辞职
                    if (!req.getOperatorId().equals(member.getAppointedBy()) && !req.getOperatorId().equals(req.getSurvivorId())) {
                        return Mono.error(new BusinessException("无权进行此操作"));
                    }

                    member.setStatus(0);
                    member.setTermEndTime(LocalDateTime.now());

                    ChangeGradeReq changeGradeReq = new ChangeGradeReq();
                    changeGradeReq.setSurvivorId(req.getSurvivorId());
                    changeGradeReq.setGrade(0);

                    return memberRepository.save(member)
                            .then(survivorClient.updateSurvivorGrade(changeGradeReq)); // 0 为 CIVILIAN
                }).then();
    }
}
