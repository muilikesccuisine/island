package com.my.task.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("task")
@Data
public class Task {
    @Id
    private Long id;
    private String title;
    private String description;
    private String remark;
    private String type;
    private String priority;
    
    @Column("is_mandatory")
    private Integer isMandatory;
    
    @Column("min_participants")
    private Integer minParticipants;
    
    @Column("max_participants")
    private Integer maxParticipants;
    
    @Column("initiator_id")
    private Long initiatorId;
    
    @Column("initiator_type")
    private String initiatorType;
    
    private String status;
    private LocalDateTime deadline;
    
    @Column("rewards_config")
    private String rewardsConfig;

    @Column("reference_id")
    private Long referenceId;

    @Column("reward_status")
    private Integer rewardStatus;

    @Column("is_public_enroll")
    private Integer isPublicEnroll;

    @Column("created_time")
    private LocalDateTime createdTime;
    
    @Column("updated_time")
    private LocalDateTime updatedTime;
}
