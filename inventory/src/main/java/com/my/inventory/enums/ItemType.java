package com.my.inventory.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ItemType {
    FOOD(0, "食物"),
    DRINK(1, "饮水"),
    MATERIAL(2, "材料"),
    TOOL(3, "工具"),
    MEDICINE(4, "药品"),
    WEAPON(5, "武器"),
    OTHER(6, "其他");

    private final int code;
    private final String desc;

    public static ItemType fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return OTHER;
        }
        for (ItemType value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return OTHER;
    }
}
