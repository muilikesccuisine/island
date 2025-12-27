package com.my.governance.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("election_candidate")
@Data
public class ElectionCandidate implements Persistable<Long> {
    @Id
    private Long id;

    private Long electionId;

    private Long candidateId;

    private String manifesto;

    private Integer votesCount;

    @CreatedDate
    private LocalDateTime createdTime;

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
