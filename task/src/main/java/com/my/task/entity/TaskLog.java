package com.my.task.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("task_log")
@Data
public class TaskLog {
    @Id
    private Long id;

    private Long taskId;

    private Long operatorId;
    
    private Integer action;
    private String details;

    @CreatedDate
    private LocalDateTime createdTime;
}
