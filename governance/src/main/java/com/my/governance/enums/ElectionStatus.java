package com.my.governance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum ElectionStatus {
    PREPARING(0, "报名中"),
    VOTING(1, "投票中"),
    FINISHED(2, "已结束"),
    VOID(3, "已作废");

    private final Integer code;
    private final String desc;

    public static ElectionStatus fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return PREPARING;
        }
        for (ElectionStatus value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return PREPARING;
    }
}
