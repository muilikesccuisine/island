package com.my.task.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("task")
@Data
public class Task implements Persistable<Long> {
    @Id
    private Long id;
    private Long referenceId;

    private String title;
    private String description;
    private String remark;
    private Integer type;
    private Integer priority;

    private Integer isMandatory;

    private Integer isPublicEnroll;

    private Integer minParticipants;

    private Integer maxParticipants;

    private Long initiatorId;

    private Integer initiatorType;
    
    private Integer status;
    private LocalDateTime deadline;

    private String rewardsConfig;

    private Integer rewardStatus;

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
