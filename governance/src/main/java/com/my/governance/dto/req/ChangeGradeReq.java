package com.my.governance.dto.req;

import lombok.Data;

@Data
public class ChangeGradeReq {
    private Long survivorId;
    private Integer grade;
}
