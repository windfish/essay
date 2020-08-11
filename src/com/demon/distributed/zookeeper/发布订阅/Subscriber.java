package com.demon.distributed.zookeeper.发布订阅;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 订阅者
 */
public class Subscriber {

    private static final String ZK_HOST = "localhost:2181,localhost:2182,localhost:2183";
    private static final int TIME_OUT = 4000;

    private static final String PARENT_NODE = "/publish_parent";

    private static ZooKeeper zooKeeper = null;

    private static CountDownLatch latch = new CountDownLatch(1);

    private static List<String> oldNews = null;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        zooKeeper = new ZooKeeper(ZK_HOST, TIME_OUT, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if(latch.getCount() > 0 && event.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("创建会话链接成功");
                    try {
                        oldNews = zooKeeper.getChildren(PARENT_NODE, false);
                        System.out.println("oldNews.size: " + oldNews.size());
                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    latch.countDown();
                }

                // 处理监听
                String path = event.getPath();
                Event.EventType eventType = event.getType();

                // 若事件的路径为父目录，且事件类型为子节点变更
                if(PARENT_NODE.equals(path) && eventType == Event.EventType.NodeChildrenChanged){
                    System.out.println(PARENT_NODE + "\t" + eventType);

                    try {
                        List<String> news = zooKeeper.getChildren(PARENT_NODE, false);
                        System.out.println("news.size: " + news.size());

                        // 找出新增的消息
                        for(String s: news){
                            if(!oldNews.contains(s)){
                                byte[] data = zooKeeper.getData(PARENT_NODE + "/" + s, false, null);
                                System.out.println("收到了新消息：" + new String(data));
                            }
                        }
                        oldNews = news;
                        // 循环监听
                        zooKeeper.getChildren(PARENT_NODE, true);

                    } catch (KeeperException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        latch.await();

        List<ACL> acls = ZooDefs.Ids.OPEN_ACL_UNSAFE;
        CreateMode mode = CreateMode.PERSISTENT;

        Stat exists = zooKeeper.exists(PARENT_NODE, false);
        if(exists == null){
            zooKeeper.create(PARENT_NODE, PARENT_NODE.getBytes(), acls, mode);
        }

        // 注册监听
        zooKeeper.getChildren(PARENT_NODE, true);

        Thread.sleep(Integer.MAX_VALUE);

        zooKeeper.close();
    }

}
