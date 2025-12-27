package com.my.survivor.repository;

import com.my.survivor.entity.SurvivorPointLog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SurvivorPointLogRepository extends R2dbcRepository<SurvivorPointLog, Long> {

    // 查询某人的积分流水 (通常需要配合 Pageable 分页)
    Flux<SurvivorPointLog> findBySurvivorIdOrderByCreatedAtDesc(Long survivorId, Pageable pageable);
}