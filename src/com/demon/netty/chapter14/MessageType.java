package com.demon.netty.chapter14;

/**
 * @author xuliang
 * @since 2018/2/23 10:33
 */
public enum MessageType {
    BUSI_REQ(0, "业务请求消息"),
    BUSI_RESP(1, "业务应答消息"),
    BUSI_ONE_WAY(2, "业务ONE WAY 消息（既是请求又是响应消息）"),

    LOGIN_REQ(3, "握手请求消息"),
    LOGIN_RESP(4, "握手应答消息"),

    HEARTBEAT_REQ(5, "心跳请求消息"),
    HEARTBEAT_RESP(6, "心跳应答消息"),
    ;

    private int code;
    private String remark;

    MessageType(int code, String remark) {
        this.code = code;
        this.remark = remark;
    }

    public int code(){
        return code;
    }

    public String remark(){
        return remark;
    }

    @Override
    public String toString() {
        return "MessageType{" +
                "code=" + code +
                ", remark='" + remark + '\'' +
                '}';
    }
}
