package com.demon.distributed.zookeeper.发布订阅;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 发布者
 */
public class Publisher {

    private static final String ZK_HOST = "localhost:2181,localhost:2182,localhost:2183";
    private static final int TIME_OUT = 4000;

    private static final String PARENT_NODE = "/publish_parent";

    private static long seq = System.currentTimeMillis();
    private static final String SUB_NODE = PARENT_NODE + "/info" + seq;

    private static final String PUBLISH_INFO = "localhost，com.demon.zk1,getName,zk1," + seq;

    private static ZooKeeper zooKeeper;

    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        zooKeeper = new ZooKeeper(ZK_HOST, TIME_OUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if(latch.getCount() > 0 && event.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("创建会话链接成功");
                    latch.countDown();
                }
            }
        });

        List<ACL> acls = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        CreateMode mode = CreateMode.PERSISTENT;

        // 父节点不存在，则创建
        Stat exists = zooKeeper.exists(PARENT_NODE, false);
        if(exists == null){
            zooKeeper.create(PARENT_NODE, PARENT_NODE.getBytes(), acls, mode);
        }

        // 发布消息
        zooKeeper.create(SUB_NODE, PUBLISH_INFO.getBytes(), acls, mode);

        zooKeeper.close();
    }

}
