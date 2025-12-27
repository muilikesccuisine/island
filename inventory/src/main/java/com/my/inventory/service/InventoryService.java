package com.my.inventory.service;

import com.my.inventory.dto.rep.ItemDefinitionRep;
import com.my.inventory.dto.req.ItemDefinitionCreateReq;
import com.my.inventory.dto.req.ItemDefinitionUpdateReq;
import reactor.core.publisher.Mono;

public interface InventoryService {

    Mono<ItemDefinitionRep> createItem(long survivorId, ItemDefinitionCreateReq req);

    Mono<Void> updateItem(long survivorId, ItemDefinitionUpdateReq req);
}
