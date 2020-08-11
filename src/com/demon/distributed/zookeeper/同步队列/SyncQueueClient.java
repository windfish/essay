package com.demon.distributed.zookeeper.同步队列;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * 模拟同步队列
 *
 * 监听父节点，当子节点数量达到一定的数量时，才执行具体的业务
 */
public class SyncQueueClient {

    private static final String ZK_HOST = "localhost:2181,localhost:2182,localhost:2183";
    private static final int TIME_OUT = 4000;

    private static final String PARENT_NODE = "/sync_queue";
    private static final int need = 3;

    private static ZooKeeper zk = null;
    private static int count = 0;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        zk = new ZooKeeper(ZK_HOST, TIME_OUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                String path = event.getPath();
                Event.EventType eventType = event.getType();

                if(PARENT_NODE.equals(path) && eventType == Event.EventType.NodeChildrenChanged){
                    try {
                        List<String> children = zk.getChildren(path, false);
                        if(children.size() == need){
                            handler(true);
                        }else if(children.size() < need){
                            if(count == need){
                                handler(false);
                            }else{
                                System.out.println("等待其他机器上线。。。");
                            }
                        }
                        count = children.size();

                        // 循环监听
                        zk.getChildren(PARENT_NODE, true);

                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        Stat exists = zk.exists(PARENT_NODE, false);
        if(exists == null){
            zk.create(PARENT_NODE, PARENT_NODE.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }else{
            System.out.println("父节点已存在");
        }

        // 监听父节点
        zk.getChildren(PARENT_NODE, true);

        Thread.sleep(Integer.MAX_VALUE);

        zk.close();
    }

    private static void handler(boolean flag){
        if(flag){
            System.out.println("处理业务。。。");
        }else{
            System.out.println("停止处理业务");
        }
    }

}
