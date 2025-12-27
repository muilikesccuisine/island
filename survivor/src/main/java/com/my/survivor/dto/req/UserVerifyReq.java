package com.my.survivor.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserVerifyReq {
    @NotBlank
    private String userId;
    @NotBlank
    private String password;
}