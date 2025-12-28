package com.my.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum YesOrNo {

    YES(1, "是"),
    NO(0, "否");

    private final Integer code;
    private final String desc;

    public static YesOrNo fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return NO;
        }
        for (YesOrNo value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return NO;
    }
}
