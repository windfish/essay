package com.demon.distributed.zookeeper.同步队列;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * 同步队列、分布式栅栏
 *
 * 用来记录一台上线的服务器
 */
public class SyncQueueServer {

    private static final String ZK_HOST = "localhost:2181,localhost:2182,localhost:2183";
    private static final int TIME_OUT = 4000;

    private static final String PARENT_NODE = "/sync_queue";
    private static final String HOSTNAME = "host4";

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        ZooKeeper zk = new ZooKeeper(ZK_HOST, TIME_OUT, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });

        Stat exists = zk.exists(PARENT_NODE, false);
        if(exists == null){
            System.out.println("创建父节点");
            zk.create(PARENT_NODE, PARENT_NODE.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }else{
            System.out.println("已存在，无需创建");
        }

        // 往父节点下添加子节点，用来记录新上线的服务器
        String path = zk.create(PARENT_NODE + "/" + HOSTNAME, HOSTNAME.getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println("当前上线的服务器是：" + HOSTNAME + "， 当前服务器注册的子节点是：" + path);

        Thread.sleep(Integer.MAX_VALUE);

        zk.close();
    }

}
