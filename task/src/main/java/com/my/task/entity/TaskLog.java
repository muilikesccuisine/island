package com.my.task.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("task_log")
@Data
public class TaskLog {
    @Id
    private Long id;
    
    @Column("task_id")
    private Long taskId;
    
    @Column("operator_id")
    private Long operatorId;
    
    private String action;
    private String details;
    
    @Column("created_time")
    private LocalDateTime createdTime;
}
