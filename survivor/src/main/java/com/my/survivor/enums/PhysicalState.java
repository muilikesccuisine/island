package com.my.survivor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 身体状况
 * 对应 DB 字段 survivor.physical_state (INT)
 * HEALTHY(3), SICK(2), INJURED(1), DECEASED(0)
 */
@Getter
@AllArgsConstructor
public enum PhysicalState {
    DECEASED(0, "死亡"),
    INJURED(1, "受伤"),   // 不可从事重体力
    SICK(2, "生病"),      // 不可从事炊事
    HEALTHY(3, "健康");

    private final Integer code;
    private final String desc;

    // 方便从数据库数值转换为枚举对象
    public static PhysicalState fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return HEALTHY;
        }
        for (PhysicalState value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return HEALTHY; // 默认值
    }
}