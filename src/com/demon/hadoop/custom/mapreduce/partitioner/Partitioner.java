package com.demon.hadoop.custom.mapreduce.partitioner;

/**
 * 分区器
 */
public class Partitioner {

    private int partitionNum;

    public Partitioner() {
    }

    public Partitioner(int partitionNum) {
        this.partitionNum = partitionNum;
    }

    /**
     * 计算具体分区
     */
    public int getPartition(String key, int value, int partitionNum){
        return (key.hashCode() & Integer.MAX_VALUE) % partitionNum;
    }

    public int getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(int partitionNum) {
        this.partitionNum = partitionNum;
    }
}
