package com.my.governance.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AdminReq {

    @NotNull(message = "survivorId不能为空")
    private Long survivorId;

    @NotNull(message = "operatorId不能为空")
    private Long operatorId;

}
