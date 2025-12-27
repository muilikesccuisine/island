package com.my.survivor.repository;

import com.my.survivor.entity.SurvivorRelationship;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface SurvivorRelationshipRepository extends R2dbcRepository<SurvivorRelationship, Long> {

    // 查询某人的所有亲属关系
    Flux<SurvivorRelationship> findBySurvivorId(Long survivorId);
    
    // 这里不需要 findBySurvivorIdAndRelativeId，因为我们是单向存储还是双向存储待定，
    // 但通常查全家桶用 findBySurvivorId 就够了
}