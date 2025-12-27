package com.my.governance.dto.rep;

import lombok.Data;

@Data
public class ElectionCandidateRep {

    private Long id;

    private Long electionId;

    private Long candidateId;

    private String manifesto;

    private Integer votesCount;

}
