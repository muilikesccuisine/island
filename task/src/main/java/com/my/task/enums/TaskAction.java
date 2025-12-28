package com.my.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public enum TaskAction {

    PUBLISH(0, "发布"),
    ASSIGN(1, "分配"),
    REJECT(2, "拒绝"),
    AUDIT(3, "审核"),
    COMPLETE(4, "完成"),
    CANCEL(5, "取消"),
    EDIT(6, "编辑");

    private final Integer code;
    private final String desc;

    public static TaskAction fromCode(Integer code) {
        if (Objects.isNull(code)) {
            return EDIT;
        }
        for (TaskAction value : values()) {
            if (Objects.equals(value.code, code)) {
                return value;
            }
        }
        return EDIT;
    }

}
