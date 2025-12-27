package com.my.governance.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import java.time.LocalDateTime;

@Table("governance_member")
@Data
public class GovernanceMember implements Persistable<Long> {
    @Id
    private Long id;

    private Long survivorId;

    private Integer role; // OWNER, ADMIN

    private Integer status; // 1-在职, 0-离职

    private Long appointedBy;

    private LocalDateTime appointTime;

    private LocalDateTime termEndTime;

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
