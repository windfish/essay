package com.demon.hadoop.custom.mapreduce.task;

public class Record {

    private String key;
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
