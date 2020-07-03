package com.demon.hadoop.custom.simple_rpc.service;

import java.util.Date;

public interface DateTimeService {

    String hello(String name);

    /**
     * 获取现在的时间
     */
    String getNow();

    /**
     * 转换日期格式
     */
    String format(String format, Date date);

    String format(Date date);

}
