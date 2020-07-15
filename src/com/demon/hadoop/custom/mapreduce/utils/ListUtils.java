package com.demon.hadoop.custom.mapreduce.utils;

import java.util.List;

public class ListUtils {

    public static <T> void printList(List<T> list){
        for (T t: list){
            System.out.println(t.toString());
        }
    }
}
