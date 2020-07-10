package com.demon.hadoop.custom.rpc_simple.server;

import com.demon.hadoop.custom.rpc_simple.utils.Constants;

public class ServerBootstrap {

    public static void main(String[] args) {
        Server server = new Server(Constants.HOST, Constants.PORT);

        server.start();
    }
}
