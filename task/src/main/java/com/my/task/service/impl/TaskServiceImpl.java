package com.my.task.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my.common.exception.BusinessException;
import com.my.common.util.IdUtils;
import com.my.task.client.SurvivorClient;
import com.my.task.dto.req.TaskAuditReq;
import com.my.task.dto.req.TaskPublishReq;
import com.my.task.dto.req.TaskSubmitReq;
import com.my.task.entity.Task;
import com.my.task.entity.TaskLog;
import com.my.task.entity.TaskParticipant;
import com.my.task.repository.TaskLogRepository;
import com.my.task.repository.TaskParticipantRepository;
import com.my.task.repository.TaskRepository;
import com.my.task.service.RewardHandler;
import com.my.task.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final TaskParticipantRepository participantRepository;
    private final TaskLogRepository logRepository;
    private final SurvivorClient survivorClient;
    private final Map<String, RewardHandler> rewardHandlers;
    private final ObjectMapper objectMapper;

    public TaskServiceImpl(TaskRepository taskRepository,
                           TaskParticipantRepository participantRepository,
                           TaskLogRepository logRepository,
                           SurvivorClient survivorClient,
                           List<RewardHandler> handlers,
                           ObjectMapper objectMapper) {
        this.taskRepository = taskRepository;
        this.participantRepository = participantRepository;
        this.logRepository = logRepository;
        this.survivorClient = survivorClient;
        this.rewardHandlers = handlers.stream()
                .collect(Collectors.toMap(RewardHandler::getType, h -> h));
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Mono<Task> publishTask(TaskPublishReq req, Long operatorId) {
        // 1. 权限校验 (岛主=2, 管理员=1)
        return survivorClient.getGrade(operatorId)
                .flatMap(result -> {
                    if (result.getCode() != 200 || result.getData() < 1) {
                        return Mono.error(new BusinessException("无权发布任务"));
                    }

                    // 2. 创建任务
                    Task task = new Task();
                    task.setId(IdUtils.getSnowflakeId());
                    task.setTitle(req.getTitle());
                    task.setDescription(req.getDescription());
                    task.setType(req.getType());
                    task.setPriority(req.getPriority());
                    task.setIsMandatory(req.getIsMandatory());
                    task.setDeadline(req.getDeadline());
                    task.setIsPublicEnroll(req.getIsPublicEnroll());
                    task.setInitiatorId(operatorId);
                    task.setInitiatorType(result.getData());
                    task.setStatus(1);
                    task.setRewardsConfig(req.getRewardsConfig());
                    task.setRewardStatus(0);

                    return taskRepository.save(task)
                            .flatMap(savedTask -> {
                                // 3. 如果是非公开任务，创建指派记录
                                if (req.getIsPublicEnroll() == 0 && req.getAssignedSurvivorIds() != null) {
                                    return Flux.fromIterable(req.getAssignedSurvivorIds())
                                            .flatMap(survivorId -> {
                                                TaskParticipant tp = new TaskParticipant();
                                                tp.setId(IdUtils.getSnowflakeId());
                                                tp.setTaskId(savedTask.getId());
                                                tp.setSurvivorId(survivorId);
                                                tp.setStatus(0);
                                                return participantRepository.save(tp);
                                            })
                                            .then(recordLog(savedTask.getId(), operatorId, 0, "发布并指派任务"))
                                            .thenReturn(savedTask);
                                }
                                return recordLog(savedTask.getId(), operatorId, 0, "发布公开任务")
                                        .thenReturn(savedTask);
                            });
                });
    }

    @Override
    @Transactional
    public Mono<Void> enrollTask(Long taskId, Long survivorId) {
        return taskRepository.findById(taskId)
                .switchIfEmpty(Mono.error(new BusinessException("任务不存在")))
                .flatMap(task -> {
                    if (!Objects.equals(task.getStatus(), 1)) {
                        return Mono.error(new BusinessException("任务当前不可加入"));
                    }

                    return participantRepository.findByTaskIdAndSurvivorId(taskId, survivorId)
                            .flatMap(existing -> {
                                // 如果是被指派的，接受任务
                                if (Objects.equals(existing.getStatus(), 0)) {
                                    existing.setStatus(1);
                                    return participantRepository.save(existing).then();
                                }
                                return Mono.error(new BusinessException("你已在任务中"));
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                // 如果是公开任务，直接报名
                                if (task.getIsPublicEnroll() == 1) {
                                    TaskParticipant tp = new TaskParticipant();
                                    tp.setId(IdUtils.getSnowflakeId());
                                    tp.setTaskId(taskId);
                                    tp.setSurvivorId(survivorId);
                                    tp.setStatus(1);
                                    return participantRepository.save(tp).then();
                                }
                                return Mono.error(new BusinessException("该任务仅限指派人员"));
                            }));
                });
    }

    @Override
    @Transactional
    public Mono<Void> submitTask(TaskSubmitReq req, Long survivorId) {
        return participantRepository.findByTaskIdAndSurvivorId(req.getTaskId(), survivorId)
                .switchIfEmpty(Mono.error(new BusinessException("未参与此任务")))
                .flatMap(tp -> {
                    if (req.getIsRejected()) {
                        tp.setStatus(5);
                        // 记录拒绝日志，方便管理员审计
                        return participantRepository.save(tp)
                                .then(recordLog(req.getTaskId(), survivorId, 2, "拒绝强制任务，原因: " + req.getContent()));
                    } else {
                        tp.setStatus(3);
                        tp.setSubmissionContent(req.getContent());
                        tp.setSubmissionTime(LocalDateTime.now());
                        return participantRepository.save(tp).then();
                    }
                });
    }

    @Override
    @Transactional
    public Mono<Void> auditTask(TaskAuditReq req, Long operatorId) {
        // 1. 权限校验
        return survivorClient.getGrade(operatorId)
                .flatMap(result -> {
                    if (result.getCode() != 200 || result.getData() < 1) {
                        return Mono.error(new BusinessException("无权审核任务"));
                    }

                    // 2. 获取参与记录
                    return participantRepository.findById(req.getParticipantId())
                            .switchIfEmpty(Mono.error(new BusinessException("参与记录不存在")))
                            .flatMap(tp -> {
                                if (Objects.equals(tp.getStatus(), 4)) {
                                    return Mono.error(new BusinessException("任务已结算，不可重复操作"));
                                }

                                tp.setRating(req.getRating());
                                tp.setFeedback(req.getFeedback());

                                if (req.getPassed()) {
                                    tp.setStatus(4); // 标记为已结算 (幂等)
                                    return participantRepository.save(tp)
                                            .then(taskRepository.findById(tp.getTaskId()))
                                            .flatMap(task -> executeRewards(tp.getSurvivorId(), task.getRewardsConfig()))
                                            .then(recordLog(tp.getTaskId(), operatorId, 3, "审核通过并结算奖励"));
                                } else {
                                    tp.setStatus(5);
                                    return participantRepository.save(tp)
                                            .then(recordLog(tp.getTaskId(), operatorId, 3, "审核驳回"));
                                }
                            });
                });
    }

    private Mono<Void> executeRewards(Long survivorId, String rewardsConfig) {
        if (rewardsConfig == null || rewardsConfig.isEmpty()) {
            return Mono.empty();
        }

        try {
            Map<String, Object> configMap = objectMapper.readValue(rewardsConfig, new TypeReference<>() {});
            return Flux.fromIterable(configMap.entrySet())
                    .flatMap(entry -> {
                        RewardHandler handler = rewardHandlers.get(entry.getKey());
                        if (handler != null) {
                            return handler.handle(survivorId, entry.getValue());
                        }
                        log.warn("未找到对应的奖励处理器: {}", entry.getKey());
                        return Mono.empty();
                    })
                    .then();
        } catch (Exception e) {
            log.error("解析奖励配置失败", e);
            return Mono.error(new BusinessException("奖励配置解析失败"));
        }
    }

    private Mono<Void> recordLog(Long taskId, Long operatorId, Integer action, String details) {
        TaskLog taskLog = new TaskLog();
        taskLog.setId(IdUtils.getSnowflakeId());
        taskLog.setTaskId(taskId);
        taskLog.setOperatorId(operatorId);
        taskLog.setAction(action);
        taskLog.setDetails(details);
        return logRepository.save(taskLog).then();
    }
}
