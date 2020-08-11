package com.demon.distributed.zookeeper.高可用;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * 服务器主节点
 *
 * 若A 是第一个上线的master，会自动成为active 状态
 * 若B 是第二个上线的，会自动成为standby 状态
 * 若C 是第三个上线的，会自动成为standby 状态
 * 如果A 宕机后，B 和C 会去竞选active 状态
 * 如果A 再上线，会成为standby 状态
 *
 */
public class MasterHA {

    private static final String ZK_HOST = "localhost:2181,localhost:2182,localhost:2183";
    private static final int TIME_OUT = 4000;
    private static ZooKeeper zk = null;

    private static final String PARENT = "/cluster_ha";
    private static final String ACTIVE = PARENT + "/active";
    private static final String STANDBY = PARENT + "/standby";
    private static final String LOCK = PARENT + "/lock";

    private static final String HOSTNAME = "cluster1";

    private static final String activeMasterPath = ACTIVE + "/" + HOSTNAME;
    private static final String standbyMasterPath = STANDBY + "/" + HOSTNAME;

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
        zk = new ZooKeeper(ZK_HOST, TIME_OUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                String path = event.getPath();
                Event.EventType eventType = event.getType();

                if(ACTIVE.equals(path) && eventType == Event.EventType.NodeChildrenChanged){
                    if(getChildrenNum(ACTIVE) == 0){
                        // 若发现active 节点下的active master 节点被删除之后，就应该去竞选active 了
                        try {
                            // 注册一把独占锁，多个standby 角色谁注册成功，谁就切换为active
                            zk.exists(LOCK, true);
                        } catch (KeeperException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        // 创建节点，数据存储为自己的信息
                        createZNode(LOCK, HOSTNAME, CreateMode.EPHEMERAL, "lock");
                    }

                    // 循环监听
                    try {
                        zk.getChildren(ACTIVE, true);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else if(LOCK.equals(path) && eventType == Event.EventType.NodeCreated){
                    String data = null;
                    try {
                        byte[] zdata = zk.getData(LOCK, false, null);
                        data = new String(zdata);
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if(HOSTNAME.equals(data)){
                        // lock 节点是自己创建的，表示自己竞争成功，晋升为active，向active 节点下写入自己的信息
                        createZNode(activeMasterPath, HOSTNAME, CreateMode.EPHEMERAL);

                        if(exists(activeMasterPath)){
                            System.out.println(HOSTNAME + " 成功切换为active");
                            deleteZNode(standbyMasterPath);
                        }else{
                            System.out.println(HOSTNAME + " 竞选成为active 状态");
                        }
                    }
                }
            }
        });

        if(!exists(PARENT)){
            createZNode(PARENT, PARENT, CreateMode.PERSISTENT);
        }
        if(!exists(ACTIVE)){
            createZNode(ACTIVE, ACTIVE, CreateMode.PERSISTENT);
        }
        if(!exists(STANDBY)){
            createZNode(STANDBY, STANDBY, CreateMode.PERSISTENT);
        }

        // 首先判断active 节点下是否有子节点，若有，则自己成为standby；若没有，就注册一把锁并竞争active
        if(getChildrenNum(ACTIVE) == 0){
            // 注册监听
            zk.exists(LOCK, true);
            // 争抢锁
            createZNode(LOCK, HOSTNAME, CreateMode.EPHEMERAL);
        }else{
            createZNode(standbyMasterPath, HOSTNAME, CreateMode.EPHEMERAL);
            System.out.println(HOSTNAME + " 发现active 存在，自己成为standby");
            // 监听active 节点的子节点变化，随时竞争active
            zk.getChildren(ACTIVE, true);
        }

        Thread.sleep(Integer.MAX_VALUE);
    }

    private static int getChildrenNum(String path){
        int num = 0;
        try {
            num = zk.getChildren(path, false).size();
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return num;
    }

    private static boolean exists(String path){
        Stat exists = null;
        try {
            exists = zk.exists(path, false);
        } catch (KeeperException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return exists != null;
    }

    private static void deleteZNode(String path){
        try {
            if(exists(path)) {
                zk.delete(path, -1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

    private static void createZNode(String path, String data, CreateMode mode){
        try {
            zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
        } catch (Exception e) {
            System.out.println("创建节点失败 或 节点已存在");
        }
    }

    private static void createZNode(String path, String data, CreateMode mode, String message){
        try {
            zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, mode);
        } catch (Exception e) {
            if("lock".equals(message)){
                System.out.println("我没抢到锁，等待下一波");
            }
        }
    }

}
