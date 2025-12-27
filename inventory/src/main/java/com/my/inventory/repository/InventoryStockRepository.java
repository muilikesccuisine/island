package com.my.inventory.repository;

import com.my.inventory.entity.InventoryStock;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface InventoryStockRepository extends ReactiveCrudRepository<InventoryStock, Long> {
    Mono<InventoryStock> findByItemId(Long itemId);

    @Modifying
    @Query("UPDATE inventory_stock SET quantity = quantity + :amount WHERE item_id = :itemId")
    Mono<Integer> updateStock(Long itemId, BigDecimal amount);
}
