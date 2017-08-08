package com.demon.netty.chapter10.httpfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Pattern;

import javax.activation.MimetypesFileTypeMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

/**
 * 
 * @author xuliang
 * @since 2017年8月7日 上午11:01:22
 *
 */
public class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String url;
    
    public HttpFileServerHandler(String url) {
        this.url = url;
    }
    
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
        System.out.println(req);
        
        
        // 解码失败，直接返回 HTTP 400错误
        if(!req.getDecoderResult().isSuccess()){
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
            return;
        }
        // 仅处理 GET请求
        if(req.getMethod() != HttpMethod.GET){
            sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
            return;
        }
        final String uri = req.getUri();
        final String path = sanitizeUri(uri);
        // 如果 URI不合法，返回 HTTP 403错误
        if(path == null){
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }
        System.out.println(path);
        File file = new File(path);
        // 如果文件不存在或者是隐藏文件，则返回 HTTP 404错误
        if(file.isHidden() || !file.exists()){
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        // 如果是目录，则发送目录的链接给客户端浏览器
        if(file.isDirectory()){
            if(uri.endsWith("/")){
                sendListing(ctx, file);
            }else{
                sendRedirect(ctx, uri+"/");
            }
            return;
        }
        if(!file.isFile()){
            sendError(ctx, HttpResponseStatus.FORBIDDEN);
            return;
        }
        // 如果是文件，则将文件信息发送到客户端浏览器
        RandomAccessFile randomAccessFile = null;
        try{
            randomAccessFile = new RandomAccessFile(file, "r"); // 以只读的方式打开文件
        }catch (FileNotFoundException e) {
            sendError(ctx, HttpResponseStatus.NOT_FOUND);
            return;
        }
        long fileLength = randomAccessFile.length();
        HttpResponse resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        setContentLength(resp, fileLength);
        setContentTypeHeader(resp, file);
        if(HttpHeaders.isKeepAlive(req)){
            resp.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }
        ctx.write(resp);
        ChannelFuture sendFileFuture;
        // 通过 Netty的 ChunkedFile对象将文件写入到发送缓冲区中
        sendFileFuture = ctx.write(new ChunkedFile(randomAccessFile, 0, fileLength, 8192), ctx.newProgressivePromise());
        // 监听文件是否发送完成
        sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
            
            @Override
            public void operationComplete(ChannelProgressiveFuture future) throws Exception {
                System.out.println("Transfer complete.");
            }
            
            @Override
            public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) throws Exception {
                if(total < 0){
                    System.err.println("Transfer progress: "+progress);
                }else{
                    System.err.println("Transfer progress: "+progress+"/"+total);
                }
            }
        });
        // 使用 chunked编码，最后需要发送一个编码结束的空消息体，将 lastHttpContent的 EMPTY_LAST_CONTENT发送到缓冲区中，标识所有的消息体已经发送完成
        ChannelFuture lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        // 如果是非 Keep-Alive的，最后要主动关闭连接
        if(!HttpHeaders.isKeepAlive(req)){
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if(ctx.channel().isActive()){
            sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    private static final Pattern INSECURE_URI = Pattern.compile(".*[<>&\"].*");
    private String sanitizeUri(String uri){
        try {
            // 对 URL进行解码
            uri = URLDecoder.decode(uri, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            try {
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            } catch (UnsupportedEncodingException e1) {
                throw new Error();
            }
        }
        // 校验 URL的合法性，如果 URI与允许访问的 URI一致或者是其子目录（文件），则校验通过，否则返回空
        if(!uri.startsWith(url)){
            return null;
        }
        if(!uri.startsWith("/")){
            return null;
        }
        // 替换文件路径分隔符为本地操作系统的文件路径分隔符
        uri = uri.replace('/', File.separatorChar);
        if(uri.contains(File.separator+'.')
                || uri.contains('.'+File.separator)
                || uri.startsWith(".")
                || uri.endsWith(".")
                || INSECURE_URI.matcher(uri).matches()){
            return null;
        }
        return System.getProperty("user.dir") + (uri.startsWith(File.separator) ? uri : File.separator + uri);
    }
    
    private static final Pattern ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]*");
    private void sendListing(ChannelHandlerContext ctx, File dir){
        // 构造成功的 HTTP响应消息，随后设置消息头类型为"text/html;charset=UTF-8"
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        resp.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=UTF-8");
        StringBuilder buf = new StringBuilder();
        String dirPath = dir.getPath();
        buf.append("<!DOCTYPE html>\r\n");
        buf.append("<html><head><title>");
        buf.append(dirPath);
        buf.append(" 目录: ");
        buf.append("</title></head></body>\r\n");
        buf.append("<h3>");
        buf.append(dirPath).append(" 目录： ");
        buf.append("</h3>\r\n");
        buf.append("<ul>");
        buf.append("<li>链接：<a href=\"../\">..</a></li>\r\n");
        for(File f: dir.listFiles()){
            if(f.isHidden() || !f.canRead()){
                continue;
            }
            String name = f.getName();
            if(!ALLOWED_FILE_NAME.matcher(name).matches()){
                continue;
            }
            buf.append("<li>链接：<a href=\"").append(name).append("\">").append(name).append("</a></li>\r\n");
        }
        buf.append("</ul></body></html>\r\n");
        ByteBuf buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8);
        resp.content().writeBytes(buffer);
        buffer.release();
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }
    
    private void sendRedirect(ChannelHandlerContext ctx, String newUri) {
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND);
        resp.headers().set(HttpHeaders.Names.LOCATION, newUri);
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }
    
    private void sendError(ChannelHandlerContext ctx, HttpResponseStatus status) {
        FullHttpResponse resp = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, 
                    Unpooled.copiedBuffer("Failure: "+status.toString()+"\r\n", CharsetUtil.UTF_8));
        resp.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain;charset=UTF-8");
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }
    
    private void setContentTypeHeader(HttpResponse resp, File file) {
        MimetypesFileTypeMap mimeTypeMap = new MimetypesFileTypeMap();
        resp.headers().set(HttpHeaders.Names.CONTENT_TYPE, mimeTypeMap.getContentType(file));
    }
    
    private void setContentLength(HttpResponse resp, long length) {
        resp.headers().set(HttpHeaders.Names.CONTENT_LENGTH, length);
    }
    
}
