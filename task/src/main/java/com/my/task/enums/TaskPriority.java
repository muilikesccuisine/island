package com.my.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum TaskPriority {

    LOW(0, "低"),
    NORMAL(1, "中"),
    HIGH(2, "高"),
    CRITICAL(3, "严重");

    private final Integer code;
    private final String desc;

    public static TaskPriority fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return NORMAL;
        }
        for (TaskPriority value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return NORMAL;
    }

}
