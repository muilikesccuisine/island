package com.my.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum TaskStatus {

    PENDING(0, "待启动"),
    PUBLISHED(1, "已发布"),
    IN_PROGRESS(2, "进行中"),
    COMPLETED(3, "已完成"),
    FAILED(4, "已失败"),
    CANCELLED(5, "已取消");

    private final Integer code;
    private final String desc;

    public static TaskStatus fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return PENDING;
        }
        for (TaskStatus value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return PENDING;
    }

}
