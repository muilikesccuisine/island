package com.my.governance.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table("election")
@Data
public class Election implements Persistable<Long> {
    @Id
    private Long id;

    private String title;
    private Integer status; // PREPARING, VOTING, FINISHED, VOID

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal minVoteRate;

    private Integer winStrategy;

    private Integer totalEligibleVoters;

    private Integer finalVotesCount;

    private Long winnerId;

    private Long taskId;

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
