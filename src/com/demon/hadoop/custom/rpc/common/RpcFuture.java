package com.demon.hadoop.custom.rpc.common;

import com.demon.hadoop.custom.rpc.protocol.RpcResponse;

/**
 * 通过异步的方式获取响应结果
 */
public class RpcFuture {

    private RpcResponse response;
    private volatile boolean isResponseSet = false; // 是否已经能获取到结果
    private final Object lock = new Object();

    /**
     * 获取结果
     */
    public RpcResponse getResponse(int timeout){
        synchronized (lock){
            while(!isResponseSet){
                try{
                    lock.wait(timeout);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return response;
        }
    }

    /**
     * 设置结果
     */
    public void setResponse(RpcResponse response){
        if(isResponseSet){
            return;
        }synchronized (lock){
            this.response = response;
            this.isResponseSet = true;
            lock.notify();
        }
    }
}
