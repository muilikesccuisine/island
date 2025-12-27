package com.my.governance.repository;

import com.my.governance.entity.ElectionVote;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ElectionVoteRepository extends ReactiveCrudRepository<ElectionVote, Long> {
    Mono<Long> countByElectionId(Long electionId);
    Mono<Boolean> existsByElectionIdAndVoterId(Long electionId, Long voterId);
}
