package com.my.task.service;

import reactor.core.publisher.Mono;

/**
 * 任务奖励处理策略接口
 */
public interface RewardHandler {

    /**
     * 奖励类型 (对应 JSON 中的 Key)
     */
    String getType();

    /**
     * 执行奖励发放逻辑
     * @param survivorId 幸存者ID
     * @param value JSON 中对应的值
     */
    Mono<Void> handle(Long survivorId, Object value);
}
