package com.my.survivor.repository;

import com.my.survivor.entity.SurvivorLog;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SurvivorLogRepository extends R2dbcRepository<SurvivorLog, Long> {

    // 查询某人的大事记
    Flux<SurvivorLog> findBySurvivorIdOrderByCreatedAtDesc(Long survivorId);
}