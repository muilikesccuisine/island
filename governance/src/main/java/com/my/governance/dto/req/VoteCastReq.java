package com.my.governance.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VoteCastReq {
    @NotNull
    private Long electionId;
    @NotNull
    private Long voterId;
    @NotNull
    private Long candidateId;
}
