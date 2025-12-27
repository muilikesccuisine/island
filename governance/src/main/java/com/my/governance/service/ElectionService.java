package com.my.governance.service;

import com.my.governance.dto.rep.ElectionCandidateRep;
import com.my.governance.dto.rep.ElectionRep;
import com.my.governance.dto.req.CandidateApplyReq;
import com.my.governance.dto.req.VoteCastReq;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ElectionService {
    
    /**
     * 发起竞选
     */
    Mono<ElectionCandidateRep> applyCandidate(CandidateApplyReq req);

    /**
     * 投票
     */
    Mono<Void> castVote(VoteCastReq req);

    /**
     * 获取选举详情
     */
    Mono<ElectionRep> getElection(Long electionId);

    /**
     * 获取候选人列表
     */
    Flux<ElectionCandidateRep> getCandidates(Long electionId);
}
