package com.my.inventory.service.impl;

import com.my.common.exception.BusinessException;
import com.my.common.util.IdUtils;
import com.my.inventory.dto.rep.ItemDefinitionRep;
import com.my.inventory.dto.req.ItemDefinitionCreateReq;
import com.my.inventory.dto.req.ItemDefinitionUpdateReq;
import com.my.inventory.entity.ItemDefinition;
import com.my.inventory.repository.ItemDefinitionRepository;
import com.my.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ItemDefinitionRepository itemDefinitionRepository;

    @Override
    @Transactional
    public Mono<ItemDefinitionRep> createItem(long survivorId, ItemDefinitionCreateReq req) {
        return itemDefinitionRepository.findByNameAndCode(req.getName(), req.getCode())
                .flatMap(existing -> Mono.error(new BusinessException("该物品已定义，code为：" + existing.getCode())))
                .switchIfEmpty(Mono.defer(() -> {
                    ItemDefinition item = new ItemDefinition();
                    item.setId(IdUtils.getSnowflakeId());
                    item.markAsNew();
                    item.setName(req.getName());
                    item.setCode(req.getCode());
                    item.setType(req.getType());
                    item.setUnit(req.getUnit());
                    item.setDescription(req.getDescription());
                    item.setCreatorId(survivorId);
                    item.setOperatorId(survivorId);
                    log.info("新物品记录：{} (CODE:{})", req.getName(), req.getCode());
                    return itemDefinitionRepository.save(item);
                }))
                .map(item -> {
                    ItemDefinitionRep rep = new ItemDefinitionRep();
                    BeanUtils.copyProperties(item, rep);
                    return rep;
                });
    }

    @Override
    @Transactional
    public Mono<Void> updateItem(long survivorId, ItemDefinitionUpdateReq req) {
        return itemDefinitionRepository.findById(req.getId())
                .switchIfEmpty(Mono.error(new BusinessException("物品不存在")))
                .flatMap(item -> {
                    item.setName(req.getName());
                    item.setCode(req.getCode());
                    item.setType(req.getType());
                    item.setUnit(req.getUnit());
                    item.setDescription(req.getDescription());
                    item.setOperatorId(survivorId);
                    return itemDefinitionRepository.save(item);
                })
                .then();
    }
}
