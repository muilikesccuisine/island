package com.my.governance.repository;

import com.my.governance.entity.ElectionCandidate;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ElectionCandidateRepository extends ReactiveCrudRepository<ElectionCandidate, Long> {
    Flux<ElectionCandidate> findAllByElectionId(Long electionId);
    Mono<ElectionCandidate> findByElectionIdAndCandidateId(Long electionId, Long candidateId);
}
