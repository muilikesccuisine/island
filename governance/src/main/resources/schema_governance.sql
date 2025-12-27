-- ==========================================
-- 1. 选举活动表
-- ==========================================
CREATE TABLE IF NOT EXISTS election (
    id BIGINT PRIMARY KEY COMMENT '雪花算法ID',
    title VARCHAR(100) NOT NULL COMMENT '选举标题 (如: 第一届岛主选举)',
    status VARCHAR(20) NOT NULL DEFAULT 'PREPARING' COMMENT '状态: PREPARING(报名中), VOTING(投票中), FINISHED(已结束), VOID(流选)',
    
    start_time DATETIME NOT NULL COMMENT '投票开始时间',
    end_time DATETIME NOT NULL COMMENT '投票截止时间',
    
    -- 规则控制
    min_vote_rate DECIMAL(5,2) DEFAULT 0.50 COMMENT '最低投票率要求 (默认0.50)',
    win_strategy VARCHAR(20) DEFAULT 'MOST_VOTES' COMMENT '获胜规则: MOST_VOTES, OVER_HALF',
    
    -- 统计快照
    total_eligible_voters INT NOT NULL COMMENT '发起选举时的合资格选民总数',
    final_votes_count INT DEFAULT 0 COMMENT '最终总计票数',
    
    winner_id BIGINT COMMENT '最终当选人ID (survivor_id)',
    task_id BIGINT COMMENT '关联的任务ID',
    
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选举活动表';

-- ==========================================
-- 2. 选举候选人明细表
-- ==========================================
CREATE TABLE IF NOT EXISTS election_candidate (
    id BIGINT PRIMARY KEY COMMENT '雪花算法ID',
    election_id BIGINT NOT NULL COMMENT '关联选举ID',
    candidate_id BIGINT NOT NULL COMMENT '候选人ID (survivor_id)',
    manifesto TEXT COMMENT '竞选宣言',
    votes_count INT DEFAULT 0 COMMENT '实时得票数',
    
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_election_candidate (election_id, candidate_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选举候选人表';

-- ==========================================
-- 3. 选票明细表
-- ==========================================
CREATE TABLE IF NOT EXISTS election_vote (
    id BIGINT PRIMARY KEY COMMENT '雪花算法ID',
    election_id BIGINT NOT NULL COMMENT '关联选举ID',
    voter_id BIGINT NOT NULL COMMENT '投票人ID (survivor_id)',
    candidate_id BIGINT NOT NULL COMMENT '被投人ID (candidate_id)',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    UNIQUE KEY uk_voter_election (election_id, voter_id) COMMENT '保证每人每届只能投一票'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='选票明细表';

-- ==========================================
-- 4. 治理成员表 (核心权力表)
-- ==========================================
CREATE TABLE IF NOT EXISTS governance_member (
    id BIGINT PRIMARY KEY COMMENT '雪花算法ID',
    survivor_id BIGINT NOT NULL COMMENT '幸存者ID',
    role VARCHAR(20) NOT NULL COMMENT '官职: OWNER(岛主), ADMIN(管理员)',
    status TINYINT DEFAULT 1 COMMENT '状态: 1-在职, 0-离职/撤职',
    
    appointed_by BIGINT DEFAULT 0 COMMENT '任命人ID (系统/选举产生为0)',
    appoint_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上任时间',
    term_end_time DATETIME COMMENT '卸任时间',
    
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_survivor_role (survivor_id, role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='治理成员表';
