package com.my.governance.dto.rep;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ElectionRep {

    private Long id;

    private String title;
    private Integer status; // PREPARING, VOTING, FINISHED, VOID

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private BigDecimal minVoteRate;

    private Integer winStrategy;

    private Integer totalEligibleVoters;

    private Integer finalVotesCount;

    private Long winnerId;

    private Long taskId;

}
