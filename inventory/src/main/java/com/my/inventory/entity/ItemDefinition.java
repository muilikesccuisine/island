package com.my.inventory.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("item_definition")
public class ItemDefinition implements Persistable<Long> {
    @Id
    private Long id;

    private String name;

    private String code;

    private Integer type;

    private String unit;

    private String description;

    // todo @CreatedBy 全局
    private Long creatorId;

    private Long operatorId;

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
