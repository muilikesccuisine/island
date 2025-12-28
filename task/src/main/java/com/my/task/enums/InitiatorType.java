package com.my.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum InitiatorType {

    CIVILIAN(0, "平民"),
    ADMIN(1, "管理员"),
    OWNER(2, "岛主"),
    SYSTEM(3, "系统");

    private final Integer code;
    private final String desc;

    public static InitiatorType fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return CIVILIAN;
        }
        for (InitiatorType value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return CIVILIAN;
    }

}
