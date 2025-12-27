package com.my.task.dto.req;

import lombok.Data;

@Data
public class TaskAuditReq {
    private Long taskId;
    private Long participantId;
    private Boolean passed; // true-通过, false-驳回
    private Integer rating; // 1-5分
    private String feedback; // 管理员评语
}
