package com.my.inventory.repository;

import com.my.inventory.entity.ItemDefinition;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface ItemDefinitionRepository extends ReactiveCrudRepository<ItemDefinition, Long> {
    Mono<ItemDefinition> findByNameAndCode(String name, String code);
}
