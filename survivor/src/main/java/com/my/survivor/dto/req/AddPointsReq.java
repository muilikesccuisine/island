package com.my.survivor.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddPointsReq {
    @NotNull
    private Long survivorId;
    @NotNull
    private Integer points;
}
