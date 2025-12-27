package com.my.survivor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum GradeType {
    CIVILIAN(0, "平民"),
    ADMIN(1, "管理员"),
    OWNER(2, "岛主");

    private final Integer code;
    private final String desc;

    public static GradeType fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return CIVILIAN;
        }
        for (GradeType grade : values()) {
            if (grade.getCode().equals(code)) {
                return grade;
            }
        }
        return CIVILIAN;
    }
}
