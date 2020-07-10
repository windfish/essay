package com.demon.hadoop.custom.fs_distributed;

public class Server {

    private String serverPath;

    public Server() {
    }

    public Server(String serverPath) {
        this.serverPath = serverPath;
    }

    public String getServerPath() {
        return serverPath;
    }

    public void setServerPath(String serverPath) {
        this.serverPath = serverPath;
    }
}
