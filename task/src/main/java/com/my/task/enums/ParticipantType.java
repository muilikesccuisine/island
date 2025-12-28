package com.my.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ParticipantType {

    ASSIGNED(0, "已指派"),
    ACCEPTED(1, "已接受"),
    IN_PROGRESS(2, "进行中"),
    COMPLETED(3, "已完成"),
    SETTLED(4, "已结算"),
    FAILED(4, "已失败"),
    REJECTED(5, "已拒绝");

    private final Integer code;
    private final String desc;

    public static ParticipantType fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return ASSIGNED;
        }
        for (ParticipantType value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return ASSIGNED;
    }

}
