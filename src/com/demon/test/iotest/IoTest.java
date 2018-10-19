package com.demon.test.iotest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * @author xuliang
 * @since 2018年10月18日 下午5:46:54
 *
 */
public class IoTest {

    public static void main(String[] args) throws UnknownHostException, IOException {
        IoServer server = new IoServer();
        server.start();
        try(Socket client = new Socket(InetAddress.getLocalHost(), server.getPort())){
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            reader.lines().forEach(line -> System.out.println(line));
        }
    }
    
}

class IoServer extends Thread {
    private ServerSocket serverSocket;
    private ExecutorService executor = null;
    
    public int getPort(){
        return serverSocket.getLocalPort();
    }
    
    @Override
    public void run() {
        try{
            // 0 表示自动绑定一个空闲端口
            serverSocket = new ServerSocket(0);
            executor = Executors.newFixedThreadPool(8);
            while(true){
                Socket socket = serverSocket.accept();
                RequestHandler handler = new RequestHandler(socket);
                executor.execute(handler);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(serverSocket != null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

class RequestHandler extends Thread {
    private Socket socket;
    public RequestHandler(Socket socket) {
        this.socket = socket;
    }
    
    @Override
    public void run() {
        // try-with-resource 方式释放资源
        try(PrintWriter out = new PrintWriter(socket.getOutputStream())){
            out.println("Hello world");
            out.flush();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
