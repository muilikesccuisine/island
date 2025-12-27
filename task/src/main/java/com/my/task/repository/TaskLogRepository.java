package com.my.task.repository;

import com.my.task.entity.TaskLog;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TaskLogRepository extends ReactiveCrudRepository<TaskLog, Long> {
}
