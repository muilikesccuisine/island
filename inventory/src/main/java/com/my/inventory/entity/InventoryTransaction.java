package com.my.inventory.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@Table("inventory_transaction")
public class InventoryTransaction implements Persistable<Long> {
    @Id
    private Long id;

    private Long itemId;

    private Integer transactionType;

    private BigDecimal changeAmount;

    private BigDecimal balanceAfter;

    private Long relatedId;

    private String remark;

    private Long operatorId;

    @CreatedDate
    private LocalDateTime createdAt;

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
