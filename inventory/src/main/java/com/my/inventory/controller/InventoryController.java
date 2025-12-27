package com.my.inventory.controller;

import com.my.common.model.Result;
import com.my.inventory.dto.rep.ItemDefinitionRep;
import com.my.inventory.dto.req.ItemDefinitionCreateReq;
import com.my.inventory.dto.req.ItemDefinitionUpdateReq;
import com.my.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    /*
    * 新建物品种类档案
    * 用于管理物品库存
    * */
    @PostMapping("/createItem")
    public Mono<Result<ItemDefinitionRep>> createItem(@RequestHeader("HEADER_USER_ID") long survivorId, @RequestBody ItemDefinitionCreateReq req) {
        return inventoryService.createItem(survivorId, req)
                .map(Result::success);
    }

    /*
     * 修改物品种类档案
     * 用于管理物品库存
     * */
    @PostMapping("/updateItem")
    public Mono<Result<Void>> updateItem(@RequestHeader("HEADER_USER_ID") long survivorId, @RequestBody ItemDefinitionUpdateReq req) {
        return inventoryService.updateItem(survivorId, req)
                .map(Result::success);
    }



}
