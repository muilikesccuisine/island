package com.my.task.controller;

import com.my.common.model.Result;
import com.my.task.dto.req.TaskAuditReq;
import com.my.task.dto.req.TaskPublishReq;
import com.my.task.dto.req.TaskSubmitReq;
import com.my.task.entity.Task;
import com.my.task.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/publish")
    public Mono<Result<Task>> publish(@RequestBody TaskPublishReq req, @RequestHeader("HEADER_USER_ID") Long operatorId) {
        return taskService.publishTask(req, operatorId)
                .map(Result::success);
    }

    @PostMapping("/{taskId}/enroll")
    public Mono<Result<Void>> enroll(@PathVariable Long taskId, @RequestHeader("HEADER_USER_ID") Long survivorId) {
        return taskService.enrollTask(taskId, survivorId)
                .thenReturn(Result.success());
    }

    @PostMapping("/submit")
    public Mono<Result<Void>> submit(@RequestBody TaskSubmitReq req, @RequestHeader("HEADER_USER_ID") Long survivorId) {
        return taskService.submitTask(req, survivorId)
                .thenReturn(Result.success());
    }

    @PostMapping("/audit")
    public Mono<Result<Void>> audit(@RequestBody TaskAuditReq req, @RequestHeader("HEADER_USER_ID") Long operatorId) {
        return taskService.auditTask(req, operatorId)
                .thenReturn(Result.success());
    }
}
