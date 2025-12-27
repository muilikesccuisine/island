package com.my.common.util;

import java.util.UUID;

/**
 * ID 生成工具类
 */
public class IdUtils {

    // 雪花算法工作单例 (WorkerId=1, DatacenterId=1)
    // 在正式生产环境中，应该通过配置注入不同的 WorkerId 以区分不同的机器节点
    private static final SnowflakeIdWorker snowflake = new SnowflakeIdWorker(1, 1);

    /**
     * 【数据库主键专用】获取分布式唯一 ID (雪花算法)
     * 返回 Long 类型，适合 MySQL BigInt
     */
    public static long getSnowflakeId() {
        return snowflake.nextId();
    }

    /**
     * 【Token/文件名专用】获取随机 UUID，不带横线
     * 适合生成 Token、随机文件名，不适合做数据库主键
     */
    public static String simpleUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 雪花算法内部类
     */
    private static class SnowflakeIdWorker {
        // 开始时间截 (2024-01-01)
        private final long twepoch = 1704067200000L;
        // 机器id所占的位数
        private final long workerIdBits = 5L;
        // 数据标识id所占的位数
        private final long datacenterIdBits = 5L;
        // 支持的最大机器id
        private final long maxWorkerId = -1L ^ (-1L << workerIdBits);
        // 支持的最大数据标识id
        private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);
        // 序列在id中占的位数
        private final long sequenceBits = 12L;
        // 机器ID向左移12位
        private final long workerIdShift = sequenceBits;
        // 数据标识id向左移17位(12+5)
        private final long datacenterIdShift = sequenceBits + workerIdBits;
        // 时间截向左移22位(5+5+12)
        private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
        // 生成序列的掩码
        private final long sequenceMask = -1L ^ (-1L << sequenceBits);

        private long workerId;
        private long datacenterId;
        private long sequence = 0L;
        private long lastTimestamp = -1L;

        public SnowflakeIdWorker(long workerId, long datacenterId) {
            if (workerId > maxWorkerId || workerId < 0) {
                throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
            }
            if (datacenterId > maxDatacenterId || datacenterId < 0) {
                throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
            }
            this.workerId = workerId;
            this.datacenterId = datacenterId;
        }

        public synchronized long nextId() {
            long timestamp = timeGen();
            if (timestamp < lastTimestamp) {
                throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
            }
            if (lastTimestamp == timestamp) {
                sequence = (sequence + 1) & sequenceMask;
                if (sequence == 0) {
                    timestamp = tilNextMillis(lastTimestamp);
                }
            } else {
                sequence = 0L;
            }
            lastTimestamp = timestamp;
            return ((timestamp - twepoch) << timestampLeftShift)
                    | (datacenterId << datacenterIdShift)
                    | (workerId << workerIdShift)
                    | sequence;
        }

        protected long tilNextMillis(long lastTimestamp) {
            long timestamp = timeGen();
            while (timestamp <= lastTimestamp) {
                timestamp = timeGen();
            }
            return timestamp;
        }

        protected long timeGen() {
            return System.currentTimeMillis();
        }
    }
}