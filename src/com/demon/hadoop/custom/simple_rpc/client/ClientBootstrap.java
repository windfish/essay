package com.demon.hadoop.custom.simple_rpc.client;

import com.demon.hadoop.custom.simple_rpc.service.DateTimeService;
import com.demon.hadoop.custom.simple_rpc.utils.Constants;

import java.util.Date;

public class ClientBootstrap {

    public static void main(String[] args) {
        Client client = new Client(Constants.HOST, Constants.PORT);
        client.start();

        DateTimeService serviceProxy = (DateTimeService) client.getProxy(DateTimeService.class);
        String result = serviceProxy.hello("xxx");
        System.out.println("client invoke service.hello result: " + result);
    }
}
