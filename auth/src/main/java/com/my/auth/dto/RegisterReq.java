package com.my.auth.dto;

import lombok.Data;

@Data
public class RegisterReq {
    // 对应 SurvivorCreateReq 的字段
    private String name;
    private Integer gender;
    private Integer age;
    private String password;
    private Integer physicalState;
    private String medicalHistory;
}