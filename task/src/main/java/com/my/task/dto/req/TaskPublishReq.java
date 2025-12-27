package com.my.task.dto.req;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskPublishReq {
    private String title;
    private String description;
    private String type; // GATHERING, CONSTRUCTION, EXPLORATION, VOTE, etc.
    private String priority; // CRITICAL, HIGH, NORMAL, LOW
    private Integer isMandatory; // 1-是, 0-否
    private LocalDateTime deadline;
    private Integer isPublicEnroll; // 1-公开报名, 0-指定指派
    private List<Long> assignedSurvivorIds; // 如果是非公开任务，这里传指派名单
    private String rewardsConfig; // 奖励配置 JSON, 如 {"points": 10}
}
