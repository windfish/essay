package com.demon.distributed.zookeeper.分布式时序锁;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * 时序锁
 *
 * 多个客户端去父znode 下写入子znode，能写入成功的就去等待执行
 * 当上一个任务完成时，等待队列中id 最小的任务可以继续执行
 */
public class DistributeSequenceLock {

    private static final String ZK_HOST = "localhost:2181,localhost:2182,localhost:2183";
    private static final int sessionTimeout = 4000;
    private static final String PARENT_NODE = "/parent_seq_lock";
    private static final String SUB_NODE = PARENT_NODE + "/sub_seq_lock";
    private static String currentPath = "";

    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws Exception {
        DistributeSequenceLock dsl = new DistributeSequenceLock();

        // 获取zookeeper 链接
        dsl.getZookeeperConnect();

        // 父节点不存在的话，创建
        Stat exists = zooKeeper.exists(PARENT_NODE, false);
        if(exists == null){
            zooKeeper.create(PARENT_NODE, PARENT_NODE.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }

        // 监听父节点
        zooKeeper.getChildren(PARENT_NODE, true);

        // 创建子节点，类型为临时顺序节点
        currentPath = zooKeeper.create(SUB_NODE, SUB_NODE.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);

        Thread.sleep(Integer.MAX_VALUE);

        zooKeeper.close();
    }

    private void getZookeeperConnect() throws Exception {
        zooKeeper = new ZooKeeper(ZK_HOST, sessionTimeout, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("事件详情：" + event.getPath() + "\t" + event.getType());
                // 需要监听父节点，并且事件类型为子节点变化
                if(event.getType() == Event.EventType.NodeChildrenChanged && PARENT_NODE.equals(event.getPath())){
                    try {
                        // 获取父节点的所有子节点，并且继续监听父节点
                        List<String> children = zooKeeper.getChildren(PARENT_NODE, true);

                        Collections.sort(children);
                        if((PARENT_NODE + "/" + children.get(0)).equals(currentPath)){
                            handler(zooKeeper, currentPath);
                        }else{
                            System.out.println("not me handler...");
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void handler(ZooKeeper zookeeper, String server) throws Exception {
        Random random = new Random();
        int sleep = 4000;
        System.out.println(server + " is working ...");

        Thread.sleep(random.nextInt(sleep));
        zookeeper.delete(currentPath, -1);
        System.out.println(server + " is done ...");

        Thread.sleep(random.nextInt(sleep));

        currentPath = zookeeper.create(SUB_NODE, SUB_NODE.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
    }

}
