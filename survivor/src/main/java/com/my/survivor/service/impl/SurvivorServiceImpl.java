package com.my.survivor.service.impl;

import com.my.common.exception.BusinessException;
import com.my.common.util.IdUtils;
import com.my.survivor.dto.rep.SurvivorRep;
import com.my.survivor.dto.req.*;
import com.my.survivor.entity.Survivor;
import com.my.survivor.enums.GradeType;
import com.my.survivor.enums.PhysicalState;
import com.my.survivor.repository.SurvivorRepository;
import com.my.survivor.service.SurvivorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
@RequiredArgsConstructor
public class SurvivorServiceImpl implements SurvivorService {

    private final SurvivorRepository survivorRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Mono<SurvivorRep> createSurvivor(SurvivorCreateReq req) {
        // 0. 前置校验：防止同一个人重复注册
        // 这里简单地使用 姓名+年龄+性别 作为唯一性判断，实际生产中可能需要身份证号或指纹
        return survivorRepository.findByNameAndAgeAndGenderAndIsDeleted(req.getName(), req.getAge(), req.getGender(), 0)
                .flatMap(existing -> Mono.<Survivor>error(new BusinessException("该幸存者已登记，ID为: " + existing.getUserId())))
                .switchIfEmpty(Mono.defer(() -> createSurvivorInternal(req)))
                .map(survivor -> {
                    SurvivorRep rep = new SurvivorRep();
                    BeanUtils.copyProperties(survivor, rep);
                    return rep;
                });
    }

    private Mono<Survivor> createSurvivorInternal(SurvivorCreateReq req) {
        // 定义一个生成并保存的逻辑
        return Mono.defer(() -> {
                    // 1. 随机生成 CN + 4位数字
                    int randomNum = ThreadLocalRandom.current().nextInt(10000);
                    String newUserId = String.format("CN%04d", randomNum);

                    // 2. 检查该 ID 是否已被占用
                    return survivorRepository.findByUserIdAndIsDeleted(newUserId, 0)
                            .flatMap(existing -> {
                                // 如果找到了存在的用户，说明 ID 冲突，抛出一个特定异常触发重试
                                return Mono.<Survivor>error(new IllegalStateException("ID冲突重试"));
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                // 3. ID 未被占用，安全，执行保存
                                Survivor survivor = new Survivor();
                                survivor.setId(IdUtils.getSnowflakeId());
                                survivor.markAsNew();

                                survivor.setUserId(newUserId);

                                // 加密存储
                                survivor.setPassword(passwordEncoder.encode(req.getPassword()));
                                // ===============================

                                survivor.setName(req.getName());
                                survivor.setGender(req.getGender());
                                survivor.setAge(req.getAge());

                                Integer state = (req.getPhysicalState() != null)
                                        ? req.getPhysicalState()
                                        : PhysicalState.HEALTHY.getCode();
                                survivor.setPhysicalState(state);
                                survivor.setMedicalHistory(req.getMedicalHistory());

                                survivor.setIslandGrade(GradeType.CIVILIAN.getCode());
                                survivor.setContributionPoint(0);
                                survivor.setIsDeleted(0);

                                log.info("新幸存者登岛: {} (ID:{})", req.getName(), newUserId);

                                // 保存到数据库
                                return survivorRepository.save(survivor);
                            }));
                })
                // 4. 遇到异常时，进行重试，最多重试 5 次
                .retry(5)
                // 5. 如果 5 次都运气不好撞车了（极小概率），抛出业务异常
                .onErrorResume(ex -> {
                    if (ex instanceof IllegalStateException) {
                        return Mono.error(new BusinessException("系统繁忙，分配身份ID失败，请稍后重试"));
                    }
                    return Mono.error(ex);
                });
    }

    @Override
    public Mono<SurvivorRep> getById(Long id) {
        return survivorRepository.findById(id)
                .switchIfEmpty(Mono.error(new BusinessException("档案不存在: " + id)))
                .map(survivor -> {
                    SurvivorRep rep = new SurvivorRep();
                    BeanUtils.copyProperties(survivor, rep);
                    return rep;
                });
    }

    @Override
    public Mono<SurvivorRep> getByUserId(String userId) {
        return survivorRepository.findByUserIdAndIsDeleted(userId, 0)
                .map(survivor -> {
                    SurvivorRep rep = new SurvivorRep();
                    BeanUtils.copyProperties(survivor, rep);
                    return rep;
                });
    }

    @Override
    public Mono<Void> changePassword(String userId, ChangePasswordReq req) {
        return survivorRepository.findByUserIdAndIsDeleted(userId, 0)
                .switchIfEmpty(Mono.error(new BusinessException("用户不存在")))
                .flatMap(survivor -> {
                    // 1. 校验旧密码
                    // passwordEncoder.matches(raw, encoded)
                    if (!passwordEncoder.matches(req.getOldPassword(), survivor.getPassword())) {
                        return Mono.error(new BusinessException("旧密码错误"));
                    }

                    // 2. 校验新密码不能与旧密码相同 (可选增强体验)
                    if (passwordEncoder.matches(req.getNewPassword(), survivor.getPassword())) {
                        return Mono.error(new BusinessException("新密码不能与旧密码相同"));
                    }

                    // 3. 加密新密码并更新
                    survivor.setPassword(passwordEncoder.encode(req.getNewPassword()));

                    // 4. 保存入库
                    return survivorRepository.save(survivor);
                })
                .then(); // 转换为 Mono<Void>，表示只关心完成与否，不需要返回值
    }

    @Override
    public Mono<Void> updateProfile(String userId, SurvivorUpdateReq req) {
        return survivorRepository.findByUserIdAndIsDeleted(userId, 0)
                .switchIfEmpty(Mono.error(new BusinessException("用户不存在")))
                .flatMap(survivor -> {
                    // 只更新允许更新的字段
                    if (req.getAvatarUrl() != null) {
                        survivor.setAvatarUrl(req.getAvatarUrl());
                    }

                    // 这里可以加日志："用户XXX更新了头像"

                    return survivorRepository.save(survivor);
                })
                .then();
    }

    @Override
    public Mono<SurvivorRep> verifyPassword(UserVerifyReq req) {
        return survivorRepository.findByUserIdAndIsDeleted(req.getUserId(), 0)
                .switchIfEmpty(Mono.error(new BusinessException("用户不存在")))
                .flatMap(survivor -> {
                    if (!passwordEncoder.matches(req.getPassword(), survivor.getPassword())) {
                        return Mono.error(new BusinessException("密码错误"));
                    }
                    SurvivorRep rep = new SurvivorRep();
                    BeanUtils.copyProperties(survivor, rep);
                    return Mono.just(rep);
                });
    }

    @Override
    public Mono<Void> changeGrade(ChangeGradeReq req) {
        return survivorRepository.findByIdAndIsDeleted(req.getSurvivorId(), 0)
                .switchIfEmpty(Mono.error(new BusinessException("用户不存在")))
                .flatMap(survivor -> {
                    survivor.setIslandGrade(req.getGrade());
                    return survivorRepository.save(survivor);
                })
                .then();
    }

    @Override
    public Mono<Integer> getSurvivorCount() {
        return survivorRepository.countByIsDeleted(0);
    }

    @Override
    public Mono<Integer> getGrade(Long id) {
        return survivorRepository.findById(id)
                .map(Survivor::getIslandGrade)
                .switchIfEmpty(Mono.error(new BusinessException("用户不存在")));
    }

    @Override
    @Transactional
    public Mono<Void> addPoints(AddPointsReq req) {
        return survivorRepository.findById(req.getSurvivorId())
                .switchIfEmpty(Mono.error(new BusinessException("用户不存在")))
                .flatMap(survivor -> {
                    int currentPoints = survivor.getContributionPoint() != null ? survivor.getContributionPoint() : 0;
                    survivor.setContributionPoint(currentPoints + req.getPoints());
                    log.info("幸存者 {} (ID:{}) 增加贡献点: {}，当前总计: {}", 
                            survivor.getName(), survivor.getId(), req.getPoints(), survivor.getContributionPoint());
                    return survivorRepository.save(survivor);
                })
                .then();
    }
}