package com.demon.netty.chapter13;

import java.io.File;
import java.io.RandomAccessFile;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.DefaultFileRegion;
import io.netty.channel.FileRegion;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 
 * @author xuliang
 * @since 2017年12月20日 上午11:49:22
 *
 */
public class FileServerHandler extends SimpleChannelInboundHandler<String> {

    private static final String CR = System.getProperty("line.separator");
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        System.out.println("msg: "+msg);
        File file = new File(msg);
        if(file.exists()){
            if(!file.isFile()){
                ctx.writeAndFlush("Not a file: "+file+CR);
                return;
            }
            ctx.write(file+" "+file.length()+CR);
            /*使用 RandomAccessFile 以只读的方式打开文件*/
            RandomAccessFile accessFile = new RandomAccessFile(msg, "r");
            /*使用 Netty 提供的 FileRegion 传输文件*/
            FileRegion region = new DefaultFileRegion(
                        accessFile.getChannel(),   // 文件通道，用于对文件进行读写操作
                        0,  // 文件操作的指针位置，读取或写入的起始点
                        accessFile.length() // 操作的总字节数
                    );
            ctx.write(region);
            ctx.writeAndFlush(CR);
            accessFile.close();
        }else{
            ctx.writeAndFlush("File not found: "+file+CR);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
