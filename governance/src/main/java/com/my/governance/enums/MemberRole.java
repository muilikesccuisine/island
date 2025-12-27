package com.my.governance.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum MemberRole {

    ADMIN(1, "管理员"),
    OWNER(2, "岛主");

    private final Integer code;
    private final String desc;

    public static MemberRole fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return ADMIN;
        }
        for (MemberRole value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return ADMIN;
    }

}
