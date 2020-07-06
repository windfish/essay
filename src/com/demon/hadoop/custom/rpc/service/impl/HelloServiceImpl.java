package com.demon.hadoop.custom.rpc.service.impl;

import com.demon.hadoop.custom.rpc.service.HelloService;

public class HelloServiceImpl implements HelloService {

    @Override
    public String hello(String name) {
        return "Server: Hello " + name;
    }
}
