package com.my.governance.service.impl;

import com.my.common.exception.BusinessException;
import com.my.common.util.IdUtils;
import com.my.governance.client.SurvivorClient;
import com.my.governance.dto.rep.ElectionCandidateRep;
import com.my.governance.dto.rep.ElectionRep;
import com.my.governance.dto.req.CandidateApplyReq;
import com.my.governance.dto.req.ChangeGradeReq;
import com.my.governance.dto.req.VoteCastReq;
import com.my.governance.entity.Election;
import com.my.governance.entity.ElectionCandidate;
import com.my.governance.entity.ElectionVote;
import com.my.governance.repository.ElectionCandidateRepository;
import com.my.governance.repository.ElectionRepository;
import com.my.governance.repository.ElectionVoteRepository;
import com.my.governance.service.ElectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ElectionServiceImpl implements ElectionService {

    private final ElectionRepository electionRepository;
    private final ElectionCandidateRepository candidateRepository;
    private final ElectionVoteRepository voteRepository;
    private final SurvivorClient survivorClient;

    @Override
    public Mono<ElectionCandidateRep> applyCandidate(CandidateApplyReq req) {
        return electionRepository.findById(req.getElectionId())
                .filter(e -> Objects.equals(e.getStatus(), 0))
                .switchIfEmpty(Mono.error(new BusinessException("当前不在报名阶段")))
                .flatMap(e -> candidateRepository.findByElectionIdAndCandidateId(req.getElectionId(), req.getSurvivorId())
                        .flatMap(exists -> Mono.<ElectionCandidate>error(new BusinessException("你已经报名了")))
                        .switchIfEmpty(Mono.defer(() -> {
                            ElectionCandidate candidate = new ElectionCandidate();
                            candidate.setId(IdUtils.getSnowflakeId());
                            candidate.markAsNew();
                            candidate.setElectionId(req.getElectionId());
                            candidate.setCandidateId(req.getSurvivorId());
                            candidate.setManifesto(req.getManifesto());
                            candidate.setVotesCount(0);
                            return candidateRepository.save(candidate);
                        })))
                .map(candidate -> {
                    ElectionCandidateRep rep = new ElectionCandidateRep();
                    BeanUtils.copyProperties(candidate, rep);
                    return rep;
                });
    }

    @Override
    @Transactional
    public Mono<Void> castVote(VoteCastReq req) {
        return electionRepository.findById(req.getElectionId())
                .filter(e -> Objects.equals(e.getStatus(), 1))
                .switchIfEmpty(Mono.error(new BusinessException("选举不在投票中")))
                .flatMap(election -> voteRepository.existsByElectionIdAndVoterId(req.getElectionId(), req.getVoterId())
                        .flatMap(exists -> {
                            if (exists) return Mono.error(new BusinessException("你已经投过票了"));

                            ElectionVote vote = new ElectionVote();
                            vote.setId(IdUtils.getSnowflakeId());
                            vote.markAsNew();
                            vote.setElectionId(req.getElectionId());
                            vote.setVoterId(req.getVoterId());
                            vote.setCandidateId(req.getCandidateId());

                            return voteRepository.save(vote)
                                    .then(updateCandidateVoteCount(req.getElectionId(), req.getCandidateId()))
                                    .then(checkEarlyWinner(election));
                        })
                ).then();
    }

    private Mono<Void> updateCandidateVoteCount(Long electionId, Long candidateId) {
        return candidateRepository.findByElectionIdAndCandidateId(electionId, candidateId)
                .flatMap(c -> {
                    c.setVotesCount(c.getVotesCount() + 1);
                    return candidateRepository.save(c);
                }).then();
    }

    private Mono<Void> checkEarlyWinner(Election election) {
        return candidateRepository.findAllByElectionId(election.getId())
                .collectList()
                .flatMap(candidates -> {
                    if (candidates.isEmpty()) return Mono.empty();
                    
                    // 排序找到第一和第二名
                    candidates.sort((a, b) -> b.getVotesCount().compareTo(a.getVotesCount()));
                    ElectionCandidate first = candidates.get(0);
                    ElectionCandidate second = candidates.size() > 1 ? candidates.get(1) : null;

                    return voteRepository.countByElectionId(election.getId())
                            .flatMap(totalVoted -> {
                                int remainingVotes = election.getTotalEligibleVoters() - totalVoted.intValue();
                                int secondPlaceVotes = (second != null) ? second.getVotesCount() : 0;

                                // 提前胜选逻辑: 第一名票数 > 第二名票数 + 剩余未投人数
                                if (first.getVotesCount() > (secondPlaceVotes + remainingVotes)) {
                                    return finalizeElection(election, first.getCandidateId());
                                }
                                return Mono.empty();
                            });
                });
    }

    private Mono<Void> finalizeElection(Election election, Long winnerId) {
        election.setStatus(2);
        election.setWinnerId(winnerId);

        ChangeGradeReq req = new ChangeGradeReq();
        req.setSurvivorId(winnerId);
        req.setGrade(2);
        return electionRepository.save(election)
                .then(survivorClient.updateSurvivorGrade(req)) // 2 为 OWNER
                .then();
    }

    @Override
    public Mono<ElectionRep> getElection(Long electionId) {
        return electionRepository.findById(electionId)
                .map(election -> {
                    ElectionRep rep = new ElectionRep();
                    BeanUtils.copyProperties(election, rep);
                    return rep;
                });
    }

    @Override
    public Flux<ElectionCandidateRep> getCandidates(Long electionId) {
        return candidateRepository.findAllByElectionId(electionId)
                .map(candidate -> {
                    ElectionCandidateRep rep = new ElectionCandidateRep();
                    BeanUtils.copyProperties(candidate, rep);
                    return rep;
                });
    }
}
