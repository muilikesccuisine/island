package com.my.inventory.repository;

import com.my.inventory.entity.InventoryTransaction;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface InventoryTransactionRepository extends ReactiveCrudRepository<InventoryTransaction, Long> {
}
