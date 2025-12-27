package com.my.survivor.repository;

import com.my.survivor.entity.SurvivorSkill;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface SurvivorSkillRepository extends R2dbcRepository<SurvivorSkill, Long> {

    // 查询某人的所有技能
    Flux<SurvivorSkill> findBySurvivorId(Long survivorId);

    // 查询某人是否拥有特定技能 (例如: 检查他有没有 "医疗" 技能)
    Mono<SurvivorSkill> findBySurvivorIdAndSkillName(Long survivorId, String skillName);
}