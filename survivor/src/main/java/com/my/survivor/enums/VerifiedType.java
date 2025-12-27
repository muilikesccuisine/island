package com.my.survivor.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

/**
 * 技能真实性验证
 * 对应 DB 字段 survivor_skill.verified (TINYINT)
 */
@Getter
@AllArgsConstructor
public enum VerifiedType {
    UNVERIFIED(0, "自称"), // 尚未经过验证，可能是吹牛
    CONFIRMED(1, "公认");  // 经过大家认可或实际操作验证

    private final Integer code;
    private final String desc;

    public static VerifiedType fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return UNVERIFIED;
        }
        for (VerifiedType value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return UNVERIFIED;
    }
}
