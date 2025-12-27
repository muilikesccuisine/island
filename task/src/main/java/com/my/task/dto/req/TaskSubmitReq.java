package com.my.task.dto.req;

import lombok.Data;

@Data
public class TaskSubmitReq {
    private Long taskId;
    private String content; // 任务成果 或 拒绝理由
    private Boolean isRejected; // true-拒绝, false-完成提交
}
