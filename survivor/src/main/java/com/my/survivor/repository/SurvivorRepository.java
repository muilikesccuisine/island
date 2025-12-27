package com.my.survivor.repository;

import com.my.survivor.entity.Survivor;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SurvivorRepository extends R2dbcRepository<Survivor, Long> {

    // 根据 userId 查询当前存活的角色 (isDeleted = 0)
    Mono<Survivor> findByUserIdAndIsDeleted(String userId, Integer isDeleted);

    // 【新增】根据 姓名 + 年龄 + 性别 查找是否存在已注册的档案
    Mono<Survivor> findByNameAndAgeAndGenderAndIsDeleted(String name, Integer age, Integer gender, Integer isDeleted);

    Mono<Survivor> findByIdAndIsDeleted(Long id, Integer isDeleted);

    Mono<Integer> countByIsDeleted(int i);
}