package com.my.governance.repository;

import com.my.governance.entity.GovernanceMember;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GovernanceMemberRepository extends ReactiveCrudRepository<GovernanceMember, Long> {
    Flux<GovernanceMember> findAllByStatus(Integer status);
    Mono<GovernanceMember> findBySurvivorIdAndStatus(Long survivorId, Integer status);
}
