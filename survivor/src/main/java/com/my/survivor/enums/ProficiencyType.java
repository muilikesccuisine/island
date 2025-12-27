package com.my.survivor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ProficiencyType {
    NOVICE(0, "略懂"),
    SKILLED(1, "熟练"),
    EXPERT(2, "专业");

    private final Integer code;
    private final String desc;

    // 方便从数据库数值转换为枚举对象
    public static ProficiencyType fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return NOVICE;
        }
        for (ProficiencyType value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return NOVICE;
    }
}
