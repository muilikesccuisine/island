package com.my.governance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum WinStrategy {
    MOST_VOTE(0, "得票最多"),
    OVER_HALF(1, "已过半数");

    private final Integer code;
    private final String desc;

    public static WinStrategy fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return MOST_VOTE;
        }
        for (WinStrategy value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return MOST_VOTE;
    }
}
