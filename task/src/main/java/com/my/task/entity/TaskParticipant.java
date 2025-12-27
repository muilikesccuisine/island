package com.my.task.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("task_participant")
@Data
public class TaskParticipant {
    @Id
    private Long id;
    
    @Column("task_id")
    private Long taskId;
    
    @Column("survivor_id")
    private Long survivorId;
    
    private String status;
    
    @Column("submission_content")
    private String submissionContent;
    
    @Column("submission_time")
    private LocalDateTime submissionTime;
    
    private String feedback;
    private Integer rating;
    
    @Column("created_time")
    private LocalDateTime createdTime;
    
    @Column("updated_time")
    private LocalDateTime updatedTime;
}
