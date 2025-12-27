package com.my.survivor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum GenderType {
    MALE(1, "男"),
    FEMALE(0, "女");

    private final Integer code;
    private final String desc;

    // 方便从数据库数值转换为枚举对象
    public static GenderType fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return MALE;
        }
        for (GenderType value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return MALE;
    }
}
