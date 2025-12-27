package com.my.survivor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("survivor")
public class Survivor implements Persistable<Long> {
    @Id
    private Long id;
    private String userId;
    @JsonIgnore
    private String password;

    private String name;
    private Integer gender;
    private Integer age;

    private Integer physicalState;

    private String medicalHistory;

    private Integer islandGrade;

    private Integer contributionPoint;

    // 社交展示
    private LocalDateTime lastActiveAt;
    private String avatarUrl;

    // 系统审计
    private Integer isDeleted;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;

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
