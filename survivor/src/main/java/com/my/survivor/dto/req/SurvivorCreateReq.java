package com.my.survivor.dto.req;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SurvivorCreateReq {

    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 登录密码
     * 长度建议 6-20 位
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度请在6-20位之间")
    private String password;

    /**
     * 性别 (1:男, 0:女)
     */
    @NotNull(message = "性别不能为空")
    private Integer gender;

    /**
     * 年龄
     */
    @NotNull(message = "年龄不能为空")
    @Min(value = 0, message = "年龄必须大于等于0岁") // 顺便加个最小值校验
    @Max(value = 100, message = "年龄过大，已不适合登岛生存")
    private Integer age;

    /**
     * 身体状况 (对应 PhysicalState 枚举) - 选填
     */
    private Integer physicalState;

    /**
     * 病史/体质备注 - 选填
     */
    private String medicalHistory;

}