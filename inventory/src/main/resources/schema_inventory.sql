-- ==========================================
-- 1. 物品定义表 (基础词条)
-- ==========================================
CREATE TABLE IF NOT EXISTS item_definition (
    id BIGINT PRIMARY KEY COMMENT '物品唯一标识',
    name VARCHAR(64) NOT NULL COMMENT '物品名称: 椰子, 粗制石斧, 干净的淡水',
    code VARCHAR(32) NOT NULL UNIQUE COMMENT '物品代码: COCONUT, STONE_AXE, WATER',
    
    -- 物品类型
    -- FOOD(食物):0, DRINK(饮水):1, MATERIAL(材料/资源):2, TOOL(工具):3, MEDICINE(药品):4, WEAPON(武器):5
    type INT NOT NULL COMMENT '物品类型',
    
    -- 属性描述
    unit VARCHAR(16) DEFAULT '个' COMMENT '计量单位: 个, 升, 斤, 把',

    description VARCHAR(255) COMMENT '物品功能描述',
    
    creator_id BIGINT COMMENT '创建人ID',
    operator_id BIGINT COMMENT '最后修改人ID',
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物品定义表';

-- ==========================================
-- 2. 库存总量表 (公共/仓库)
-- ==========================================
-- 在初期，我们假设所有物资归集体管理(仓库模式)
CREATE TABLE IF NOT EXISTS inventory_stock (
    id BIGINT PRIMARY KEY,
    item_id BIGINT NOT NULL UNIQUE COMMENT '关联物品代码',
    
    quantity DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '当前库存数量',
    safe_stock_level DECIMAL(10,2) DEFAULT 0 COMMENT '安全水位(低于此值系统可自动发预警任务)',
    
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_item (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存总量表';

-- ==========================================
-- 3. 库存变动记录表 (流水)
-- ==========================================
CREATE TABLE IF NOT EXISTS inventory_transaction (
    id BIGINT PRIMARY KEY,
    item_id BIGINT NOT NULL COMMENT '物品代码',
    
    -- 变动类型
    -- TASK_REWARD(任务奖励):0, CONSUMPTION(日常消耗):1, GATHERING(采集入库):2, 
    -- LOSS(损耗/过期):3, CRAFTING(合成消耗/产出):4, MANUAL_ADJUST(人工校准):5
    transaction_type INT NOT NULL COMMENT '变动类型',
    
    change_amount DECIMAL(10,2) NOT NULL COMMENT '变动数量(正数为增，负数为减)',
    balance_after DECIMAL(10,2) NOT NULL COMMENT '变动后余额',
    
    related_id BIGINT COMMENT '关联ID (任务ID, 幸存者ID等)',
    remark VARCHAR(255) COMMENT '备注',
    
    operator_id BIGINT COMMENT '操作人ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    INDEX idx_item_time (item_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='库存变动记录表';
