package com.demon.hadoop.custom.simple_rpc.server;

import com.demon.hadoop.custom.simple_rpc.utils.Constants;

public class ServerBootstrap {

    public static void main(String[] args) {
        Server server = new Server(Constants.HOST, Constants.PORT);

        server.start();
    }
}
