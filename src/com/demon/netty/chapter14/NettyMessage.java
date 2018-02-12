package com.demon.netty.chapter14;

/**
 * 消息定义
 *
 * @author xuliang
 * @since 2018/2/12 11:21
 */
public final class NettyMessage {
    private Header header;  // 消息头
    private Object body;    // 消息体

    public final Header getHeader() {
        return header;
    }

    public final void setHeader(Header header) {
        this.header = header;
    }

    public final Object getBody() {
        return body;
    }

    public final void setBody(Object body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "NettyMessage{" +
                "header=" + header +
                '}';
    }

}
