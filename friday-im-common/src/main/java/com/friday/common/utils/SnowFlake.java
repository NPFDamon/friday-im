package com.friday.common.utils;

/**
 * Copyright (C),Damon
 *
 * @Description: twitter snow flake 主键生成器
 * @Author: Damon(npf)
 * @Date: 2020-05-22:10:18
 */
public class SnowFlake {
    /**
     * 起始时间戳
     */
    private static final long START_TIMESTAMP = 1564927230391L;

    /**
     * 每一部分占用位数
     */
    private static final long SEQUENCE_BIT = 4; //序列号
    private static final long MACHINE_BIT = 6; //机器标识
    private static final long DATA_CENTER_BIT = 2; //数据中心

    /**
     * 每一部分最大值
     */
    private static final long MAX_DATA_CENTER_NUM = -1L ^ (-1L << DATA_CENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左位移
     */
    private static final long MACHINE_LEFT = SEQUENCE_BIT;
    private static final long DATA_CENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private static final long TIMESTAMP_LEFT = DATA_CENTER_LEFT + DATA_CENTER_BIT;

    private long dataCenterId; //数据中心
    private long machineId; //机器标识
    private long sequence = 0L; //序列号
    private long lastTimestamp = -1L; //上次时间戳

    public SnowFlake(long dataCenterId,long machineId){
        if (dataCenterId > MAX_DATA_CENTER_NUM || dataCenterId < 0) {
            throw new IllegalArgumentException(
                    "dataCenterId can't be greater than MAX_DATA_CENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException(
                    "machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.dataCenterId = dataCenterId;
        this.machineId = machineId;
    }

    /**
     * 下一个id
     */
    public synchronized long nextId(){
        long currStamp = getNewTimestamp();
        if (currStamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStamp == lastTimestamp) {
            //相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            //同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStamp = getNextMill();
            }
        } else {
            //不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastTimestamp = currStamp;

        return (currStamp - START_TIMESTAMP) << TIMESTAMP_LEFT //时间戳部分
                | dataCenterId << DATA_CENTER_LEFT       //数据中心部分
                | machineId << MACHINE_LEFT             //机器标识部分
                | sequence;                             //序列号部分

    }

    public long getNextMill(){
        long mill = getNewTimestamp();
        while (mill <= lastTimestamp) {
            mill = getNewTimestamp();
        }
        return mill;
    }

    public long getNewTimestamp(){
        return System.currentTimeMillis();
    }

    public long getMachineId() {
        return machineId;
    }


    public static void main(String[] args) {
        SnowFlake snowFlake = new SnowFlake(2, 3);
        for (int i = 0; i < (1 << 12); i++) {
            System.out.println(snowFlake.nextId());
        }

    }
}
