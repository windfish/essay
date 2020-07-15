package com.demon.hadoop.custom.mapreduce.task;

/**
 * 结果对象
 */
public class Record {

    // 英文词
    private String key;
    // 词的个数
    private int value;

    @Override
    public String toString() {
        return key + "\t" + value;
    }

    public Record(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public Record() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
