package com.my.governance.controller;

import com.my.common.model.Result;
import com.my.governance.dto.rep.ElectionCandidateRep;
import com.my.governance.dto.rep.ElectionRep;
import com.my.governance.dto.req.AdminReq;
import com.my.governance.dto.req.CandidateApplyReq;
import com.my.governance.dto.req.VoteCastReq;
import com.my.governance.service.AdminService;
import com.my.governance.service.ElectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/governance")
@RequiredArgsConstructor
public class GovernanceController {

    private final ElectionService electionService;
    private final AdminService adminService;


    @PostMapping("/admin/appoint")
    public Mono<Result<Void>> appointAdmin(@RequestBody @Valid AdminReq req) {
        return adminService.appointAdmin(req)
                .map(Result::success);
    }

    @PostMapping("/admin/dismiss")
    public Mono<Result<Void>> dismissAdmin(@RequestBody @Valid AdminReq req) {
        return adminService.dismissAdmin(req)
                .map(Result::success);
    }

    @PostMapping("/applyCandidate")
    public Mono<Result<ElectionCandidateRep>> applyCandidate(@RequestBody @Valid CandidateApplyReq req) {
        return electionService.applyCandidate(req)
                .map(Result::success);
    }

    @PostMapping("/castVote")
    public Mono<Result<Void>> castVote(@RequestBody @Valid VoteCastReq req) {
        return electionService.castVote(req)
                .map(Result::success);
    }

    @GetMapping("/getElection/{electionId}")
    public Mono<Result<ElectionRep>> getElection(@PathVariable Long electionId) {
        return electionService.getElection(electionId)
                .map(Result::success);
    }

    @GetMapping("/getCandidates/{electionId}")
    public Flux<Result<ElectionCandidateRep>> getCandidates(@PathVariable Long electionId) {
        return electionService.getCandidates(electionId)
                .map(Result::success);
    }

}
