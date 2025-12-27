package com.my.auth.dto;

import lombok.Data;

@Data
public class LoginReq {
    private String userId;   // CNxxxx
    private String password;
}