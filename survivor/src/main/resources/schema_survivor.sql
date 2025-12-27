-- ==========================================
-- 1. 幸存者档案表 (身份与社会属性)
-- ==========================================
CREATE TABLE IF NOT EXISTS survivor (
    id BIGINT PRIMARY KEY COMMENT '雪花算法ID',
    user_id VARCHAR(64) COMMENT '关联的账号ID',
    password VARCHAR(255) DEFAULT NULL COMMENT '登录密码',

    -- 基础生理信息
    name VARCHAR(64) NOT NULL COMMENT '姓名',
    gender INT NOT NULL COMMENT '性别: 1(男), 0(女)',
    age INT NOT NULL COMMENT '年龄 (劳动力分配的重要依据: 儿童/青壮年/老人)',

    -- 【新增】身体状况 (不是HP，而是状态)
    -- HEALTHY(健康:3), SICK(生病-不可炊事:2), INJURED(受伤-不可重劳力:1), DECEASED(死亡:0)
    physical_state INT DEFAULT 3 COMMENT '当前身体状态',

    -- 【新增】健康备注 (关键生存信息)
    -- 例如: "哮喘", "青霉素过敏", "高度近视", "糖尿病"
    medical_history VARCHAR(255) COMMENT '病史与体质备注',

    -- 岛屿社会地位
    -- OWNER(岛主-1人):2, ADMIN(管理员-9人):1, CIVILIAN(平民-90人):0
    island_grade INT DEFAULT 0 COMMENT '社会层级',

    -- 经济/政治资本
    contribution_point INT DEFAULT 0 COMMENT '贡献点 (岛屿通货/选票)',

    last_active_at DATETIME COMMENT '最后活跃时间(用于判断失联)',
    avatar_url VARCHAR(255) COMMENT '自拍头像(便于认人)',

    -- 审计字段
    is_deleted TINYINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_user_id (user_id),
    -- 【建议】: 加上唯一约束，确保一个用户只能有一个有效角色
    UNIQUE KEY uk_user_active (user_id, is_deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='幸存者档案表';

-- ==========================================
-- 2. 幸存者技能表 (记录其实际掌握的能力)
-- ==========================================
CREATE TABLE IF NOT EXISTS survivor_skill (
    id BIGINT PRIMARY KEY,
    survivor_id BIGINT NOT NULL,

    -- 技能描述
    skill_name VARCHAR(32) NOT NULL COMMENT '技能名称: 捕鱼/生火/急救/木工/寻找水源',

    -- 掌握程度 (不使用数字等级，而是使用定性描述)
    -- NOVICE: 略懂 (看过书或视频，没实操过，"我会一点"):0
    -- SKILLED: 熟练 (有实际操作经验，"交给我没问题"):1
    -- EXPERT: 专业 (职业级水平，可以教别人，"我是这方面的专家"):2
    proficiency INT DEFAULT 0 COMMENT '熟练度',

    -- 真实性验证
    verified TINYINT DEFAULT 0 COMMENT '是否已验证(0:自称UNVERIFIED, 1:大家公认/已验证CONFIRMED)',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_survivor (survivor_id),
    -- 【建议】: 加上唯一约束，防止同一个人添加多次同一个技能
    UNIQUE KEY uk_survivor_skill (survivor_id, skill_name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='幸存者技能表';

-- ==========================================
-- 3. 贡献点流水表 (新增表)
-- ==========================================
-- 必须有流水，否则涉及吃饭和投票的分配问题一定会吵架
CREATE TABLE IF NOT EXISTS survivor_point_log (
    id BIGINT PRIMARY KEY COMMENT '主键',
    survivor_id BIGINT NOT NULL COMMENT '幸存者ID',

    change_amount INT NOT NULL COMMENT '变动数值: +10 或 -5',
    balance_after INT NOT NULL COMMENT '变动后余额(快照)',

    reason VARCHAR(255) NOT NULL COMMENT '变动原因: 伐木任务奖励, 兑换食物消耗, 违规扣除',
    operator_id BIGINT COMMENT '操作人ID(如果是管理员手动奖惩)',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_survivor (survivor_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='贡献点变动流水表';

-- ==========================================
-- 4. 亲属关系表 (户口本模式)
-- ==========================================
CREATE TABLE IF NOT EXISTS survivor_relationship (
    id BIGINT PRIMARY KEY COMMENT '雪花算法ID',
    survivor_id BIGINT NOT NULL COMMENT '档案人',
    relative_id BIGINT NOT NULL COMMENT '亲属ID',

    -- 关系类型 (法定/直系亲属)
    -- SPOUSE: 配偶 (夫妻绑定，利益共同体):0
    -- PARENT: 父母 (需赡养):1
    -- CHILD: 子女 (需抚养):2
    -- SIBLING: 兄弟姐妹 (互助):3
    relation_type INT NOT NULL COMMENT '关系类型: SPOUSE, PARENT, CHILD, SIBLING',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_relation (survivor_id, relative_id),
    INDEX idx_relative (relative_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='幸存者亲属关系表';

-- ==========================================
-- 5. 幸存者日志表 (记录关键事件，用于回溯死因或经历)
-- ==========================================
CREATE TABLE IF NOT EXISTS survivor_log (
    id BIGINT PRIMARY KEY COMMENT '雪花算法ID',
    survivor_id BIGINT NOT NULL COMMENT '幸存者ID',

    event_type INT NOT NULL COMMENT '事件类型: HEALTH_CHANGE:0, TASK_COMPLETE:1',
    message VARCHAR(255) COMMENT '日志内容: "在丛林中食用了毒蘑菇,身体状况变为SICK"',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_survivor_time (survivor_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='幸存者个人日志表';


