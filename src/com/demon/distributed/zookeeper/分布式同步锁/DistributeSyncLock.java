package com.demon.distributed.zookeeper.分布式同步锁;

import org.apache.poi.ss.formula.functions.Even;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;

/**
 * 排它锁/独占锁
 *
 * 多个角色竞争一把锁，谁创建成功znode 节点，谁拥有执行权
 */
public class DistributeSyncLock {

    // zookeeper 地址
    private static final String ZK_HOST = "localhost:2181,localhost:2182,localhost:2183";
    private static final int TIME_OUT = 4000;

    // 父子节点
    private static final String PARENT_NODE = "/parent_sync_lock";
    private static final String LOCK_NODE = PARENT_NODE + "/sync_lock";
    private static final String CURRENT_NODE = "local3";

    private static final Random random = new Random();

    // 会话对象
    private static ZooKeeper zookeeper = null;
    // 节点权限
    private static List<ACL> acls = ZooDefs.Ids.OPEN_ACL_UNSAFE;
    // 节点类型
    private static CreateMode mode = CreateMode.PERSISTENT;

    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        // 获取会话链接
        zookeeper = new ZooKeeper(ZK_HOST, TIME_OUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                // 确保链接建立
                if(latch.getCount() > 0 && event.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("创建会话链接成功！");
                    latch.countDown();
                }

                String path = event.getPath();
                Event.EventType eventType = event.getType();
                System.out.println("事件详情：" + path + "\t" + eventType);

                // 当某个任务完成后，会删除同步锁的节点，那么其余的任务都会收到通知，再次去争抢锁
                if(LOCK_NODE.equals(path) && Event.EventType.NodeDeleted.equals(eventType)){
                    try {
                        // 模拟去争抢锁资源，创建临时节点，这样的好处是线程挂掉可以自动删除节点自动释放锁
                        String node = zookeeper.create(LOCK_NODE, LOCK_NODE.getBytes(), acls, CreateMode.EPHEMERAL);
                        // 继续注册监听
                        try {
                            zookeeper.exists(LOCK_NODE, true);
                        } catch (KeeperException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // 执行业务逻辑
                        handler(zookeeper, CURRENT_NODE);

                    } catch (Exception e) {
                        System.out.println("没有抢到独占锁");
                    }
                }else if(path.equals(LOCK_NODE) && eventType.equals(Event.EventType.NodeCreated)){
                    System.out.println(CURRENT_NODE + " create " + LOCK_NODE);
                }

                // 继续监听
                try {
                    zookeeper.exists(LOCK_NODE, true);
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // zookeeper 会话建立成功，主线程恢复执行
        latch.await();

        // 判断父节点是否存在
        Stat parent = zookeeper.exists(PARENT_NODE, false);
        if(parent == null){
            zookeeper.create(PARENT_NODE, PARENT_NODE.getBytes(), acls, mode);
        }

        // 注册监听
        zookeeper.exists(LOCK_NODE, true);

        try {
            // 争抢锁，创建临时节点
            zookeeper.create(LOCK_NODE, LOCK_NODE.getBytes(), acls, CreateMode.EPHEMERAL);
            // 执行业务逻辑
            handler(zookeeper, CURRENT_NODE);

        }catch (Exception e){
            System.out.println("未抢到锁，等待下一次");
        }

        // 保持程序一直运行
        Thread.sleep(Integer.MAX_VALUE);
    }

    private static void handler(ZooKeeper zookeeper, String server){
        int sleep = 4000;
        System.out.println(server + " is working ..." + System.currentTimeMillis());
        try {
            // 线程睡眠，模拟业务处理耗时
            Thread.sleep(random.nextInt(sleep));
            // 模拟业务处理完成，删除节点，释放锁
            zookeeper.delete(LOCK_NODE, -1);

            System.out.println(server + " is done ..." + System.currentTimeMillis());

            Thread.sleep(random.nextInt(sleep));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
    }

}
