package com.my.task.service;

import com.my.task.dto.req.TaskAuditReq;
import com.my.task.dto.req.TaskPublishReq;
import com.my.task.dto.req.TaskSubmitReq;
import com.my.task.entity.Task;
import reactor.core.publisher.Mono;

public interface TaskService {

    /**
     * 发布任务 (限岛主/管理员)
     */
    Mono<Task> publishTask(TaskPublishReq req, Long operatorId);

    /**
     * 岛民领任务 / 接受指派
     */
    Mono<Void> enrollTask(Long taskId, Long survivorId);

    /**
     * 提交任务成果 / 提交拒绝理由
     */
    Mono<Void> submitTask(TaskSubmitReq req, Long survivorId);

    /**
     * 审核任务并结算奖励
     */
    Mono<Void> auditTask(TaskAuditReq req, Long operatorId);
}
