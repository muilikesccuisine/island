package com.my.survivor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum RelationType {
    SPOUSE(0, "配偶"),
    PARENT(1, "父母"),
    CHILD(2, "子女"),
    SIBLING(3, "同胞");

    private final Integer code;
    private final String desc;

    // 方便从数据库数值转换为枚举对象
    public static RelationType fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return SIBLING;
        }
        for (RelationType value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return SIBLING;
    }
}
