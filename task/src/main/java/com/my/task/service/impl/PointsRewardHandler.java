package com.my.task.service.impl;

import com.my.task.client.SurvivorClient;
import com.my.task.service.RewardHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * 贡献点奖励处理器
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class PointsRewardHandler implements RewardHandler {

    private final SurvivorClient survivorClient;

    @Override
    public String getType() {
        return "points";
    }

    @Override
    public Mono<Void> handle(Long survivorId, Object value) {
        if (!(value instanceof Integer)) {
            log.error("贡献点奖励格式错误: {}", value);
            return Mono.empty();
        }

        Integer points = (Integer) value;
        return survivorClient.addPoints(Map.of("survivorId", survivorId, "points", points))
                .flatMap(result -> {
                    if (result.getCode() != 200) {
                        return Mono.error(new RuntimeException("增加贡献点失败: " + result.getMsg()));
                    }
                    return Mono.empty();
                });
    }
}
