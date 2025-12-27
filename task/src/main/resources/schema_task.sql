-- ==========================================
-- 1. 任务主表
-- ==========================================
CREATE TABLE IF NOT EXISTS task (
    id BIGINT PRIMARY KEY COMMENT '雪花算法ID',
    reference_id BIGINT COMMENT '关联外部业务ID(如选举ID)',

    -- 任务基础信息
    title VARCHAR(100) NOT NULL COMMENT '任务标题',
    description TEXT COMMENT '任务描述',
    remark VARCHAR(255) COMMENT '备注',

    -- 任务属性
    type VARCHAR(50) NOT NULL COMMENT '任务类型: GATHERING(采集), CONSTRUCTION(建设), EXPLORATION(探索), VOTE(投票/选举), SOCIAL(社交/娱乐)',
    priority VARCHAR(20) DEFAULT 'NORMAL' COMMENT '优先级: CRITICAL, HIGH, NORMAL, LOW',
    is_mandatory TINYINT(1) DEFAULT 0 COMMENT '是否强制: 1-是, 0-否',
    is_public_enroll TINYINT DEFAULT 1 COMMENT '是否公开报名: 1-是, 0-否(仅限指派)',

    -- 任务规模与限制 (新增)
    min_participants INT DEFAULT 1 COMMENT '最少参与人数需求 (启动门槛)',
    max_participants INT DEFAULT NULL COMMENT '最大参与人数限制 (容量, NULL为不限)',

    -- 发布者快照信息 (即使人没了，发布记录还在)
    initiator_id BIGINT COMMENT '发布人ID (系统发布为0或NULL)',
    initiator_type VARCHAR(20) NOT NULL COMMENT '发布者身份类型: SYSTEM, OWNER, ADMIN, CIVILIAN',

    -- 状态与时间
    status VARCHAR(50) NOT NULL COMMENT '状态: PENDING, PUBLISHED, IN_PROGRESS, COMPLETED, FAILED, CANCELLED',
    deadline DATETIME DEFAULT NULL COMMENT '截止时间',

    -- 奖励预设 (JSON格式，预留)
    rewards_config TEXT COMMENT '任务奖励配置 JSON',
    reward_status TINYINT DEFAULT 0 COMMENT '奖励结算状态: 0-未结算, 1-已结算',

    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务主表';

-- ==========================================
-- 2. 任务参与/执行记录表
-- ==========================================
CREATE TABLE IF NOT EXISTS task_participant (
    id BIGINT PRIMARY KEY COMMENT '雪花算法ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    survivor_id BIGINT NOT NULL COMMENT '幸存者ID',

    -- 参与状态
    status VARCHAR(50) NOT NULL COMMENT '参与状态: ASSIGNED(已指派), ACCEPTED(已接受), IN_PROGRESS(进行中), COMPLETED(已完成), FAILED(失败), REJECTED(拒绝)',

    -- 提交内容
    submission_content TEXT COMMENT '提交内容(采集数量, 探索报告等)',
    submission_time DATETIME COMMENT '提交时间',

    -- 反馈/评分
    feedback VARCHAR(255) COMMENT '管理员给出的反馈',
    rating INT DEFAULT 0 COMMENT '评分(影响贡献点结算)',

    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_task_survivor (task_id, survivor_id),
    INDEX idx_survivor (survivor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务参与明细表';

-- ==========================================
-- 3. 任务日志表 (审计用)
-- ==========================================
CREATE TABLE IF NOT EXISTS task_log (
    id BIGINT PRIMARY KEY COMMENT '雪花算法ID',
    task_id BIGINT NOT NULL COMMENT '任务ID',
    operator_id BIGINT COMMENT '操作人ID (系统为0)',

    action VARCHAR(50) NOT NULL COMMENT '动作: PUBLISH, ASSIGN, COMPLETE, CANCEL, EDIT',
    details TEXT COMMENT '变更详情/备注',

    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_task (task_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务操作日志表';