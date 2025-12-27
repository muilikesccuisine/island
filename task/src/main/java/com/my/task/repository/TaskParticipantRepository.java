package com.my.task.repository;

import com.my.task.entity.TaskParticipant;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TaskParticipantRepository extends ReactiveCrudRepository<TaskParticipant, Long> {
    Mono<TaskParticipant> findByTaskIdAndSurvivorId(Long taskId, Long survivorId);
    Mono<Boolean> existsByTaskIdAndSurvivorId(Long taskId, Long survivorId);
}
