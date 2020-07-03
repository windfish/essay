package com.demon.hadoop.custom.simple_rpc.service.impl;

import com.demon.hadoop.custom.simple_rpc.service.DateTimeService;
import io.netty.util.internal.StringUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeServiceImpl implements DateTimeService {

    private String DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    private SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);

    @Override
    public String hello(String name) {
        return "I'm server, hello dear client: " + name;
    }

    @Override
    public String getNow() {
        return sdf.format(new Date());
    }

    @Override
    public String format(String format, Date date) {
        if(format == null || "".equals(format)){
            return sdf.format(date);
        }else{
            SimpleDateFormat sdf1 = new SimpleDateFormat(format);
            return sdf1.format(date);
        }
    }

    @Override
    public String format(Date date) {
        return sdf.format(date);
    }
}
