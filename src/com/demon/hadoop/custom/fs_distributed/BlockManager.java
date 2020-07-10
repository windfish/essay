package com.demon.hadoop.custom.fs_distributed;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 数据块管理
 */
public class BlockManager {

    public static List<Server> loadServers(){
        List<Server> servers = new ArrayList<>();
        String serverStr = PropertiesUtil.getProperty("servers");
        String[] ss = serverStr.split(",");
        for(String s: ss){
            servers.add(new Server(s));
        }
        return servers;
    }

    private Random r = new Random();

    /**
     * 选择存放数据块的服务器
     * @param servers
     */
    public List<Server> choseServer(List<Server> servers){
        List<Server> choseServers = new ArrayList<>();

        String blockReplication = PropertiesUtil.getProperty("block_replication");
        int blocks = Integer.parseInt(blockReplication);

        if(servers.size() <= blocks){
            return servers;
        }

        for(int i=0; i<blocks; i++){
            choseServers.add(servers.remove(r.nextInt(servers.size())));
        }
        return choseServers;
    }
}
