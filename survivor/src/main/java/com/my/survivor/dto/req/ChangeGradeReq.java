package com.my.survivor.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeGradeReq {
    @NotNull
    private Long survivorId;
    @NotNull
    private Integer grade;
}
