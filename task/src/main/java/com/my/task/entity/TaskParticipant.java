package com.my.task.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("task_participant")
@Data
public class TaskParticipant implements Persistable<Long> {
    @Id
    private Long id;

    private Long taskId;

    private Long survivorId;
    
    private Integer status;

    private String submissionContent;

    private LocalDateTime submissionTime;
    
    private String feedback;
    private Integer rating;

    @CreatedDate
    private LocalDateTime createdTime;
    
    @LastModifiedDate
    private LocalDateTime updatedTime;

    @Transient
    private boolean isNew = false;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }

    public void markAsNew() {
        this.isNew = true;
    }
}
