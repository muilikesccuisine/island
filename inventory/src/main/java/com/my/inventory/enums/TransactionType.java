package com.my.inventory.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum TransactionType {
    TASK_REWARD(0, "任务奖励"),
    CONSUMPTION(1, "日常消耗"),
    GATHERING(2, "采集入库"),
    LOSS(3, "损耗/过期"),
    CRAFTING(4, "合成消耗/产出"),
    MANUAL_ADJUST(5, "人工校准"),
    OTHER(6, "其他");

    private final int code;
    private final String desc;

    public static TransactionType fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return OTHER;
        }
        for (TransactionType value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return OTHER;
    }
}
