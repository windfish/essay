/**
 * Netty Java 序列化服务
 * 
 * ObjectDecoder 解码器
 * ObjectEncoder 编码器
 * 
 * 服务端接收客户端的用户订购消息，消息是一个对象；服务端接收消息后，校验用户名的合法性，校验通过则返回订购成功的消息给客户端，消息也是一个对象
 * 
 * 
 */
package com.demon.netty.chapter7;