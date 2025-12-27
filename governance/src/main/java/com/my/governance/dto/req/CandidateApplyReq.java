package com.my.governance.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CandidateApplyReq {

    @NotNull
    private Long electionId;
    @NotNull
    private Long survivorId;
    @NotBlank
    private String manifesto;

}
