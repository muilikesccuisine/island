package com.my.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum TaskType {

    GATHERING(0, "采集"),
    CONSTRUCTION(1, "建设"),
    EXPLORATION(2, "探索"),
    VOTE(3, "选举"),
    SOCIAL(4, "社交"),
    OTHER(5, "其他");

    private final Integer code;
    private final String desc;

    public static TaskType fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return OTHER;
        }
        for (TaskType value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return OTHER;
    }

}
