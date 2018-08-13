package com.demon.http.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpCsdnTest {

    public static void main(String[] args) throws Exception {
        String[] urls = new String[]{
                    "https://img-blog.csdn.net/20180718163444567",
                    "https://img-blog.csdn.net/20180718164236555",
                    "https://img-blog.csdn.net/2018071816402531",
                    "https://img-blog.csdn.net/2018072015440079",
                    "https://img-blog.csdn.net/20180718221753143",
                    "https://img-blog.csdn.net/20180718163650694"
                };
        
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)    // 设置连接超时时间
                .setConnectionRequestTimeout(1000)  // 设置从connect Manager(连接池)获取Connection 超时时间
                .setSocketTimeout(5000)     // 设置获取数据的超时时间
                .build();
//        HttpGet httpGet = new HttpGet("https://img-blog.csdn.net/20180718090110780");
        for(int i=0;i<urls.length;i++){
            String url = urls[i];
            String name = url.substring(26);
            System.out.println(url + " : " + name);
                    
            HttpGet httpGet = new HttpGet(url);
            System.out.println(httpGet.getURI());
            httpGet.setConfig(requestConfig);
    //        httpGet.setHeader("referer", "https://blog.csdn.net/xxx1827/article/details/81091963");
            httpGet.setHeader("referer", "https://blog.csdn.net");
            
            CloseableHttpClient client = HttpClients.createDefault();
            CloseableHttpResponse response = client.execute(httpGet);
            System.out.println(response);
            if(response != null){
                HttpEntity entity = response.getEntity();
                if(entity != null){
                    //通过输入流获取图片数据  
                    InputStream inStream = entity.getContent();  
                    //得到图片的二进制数据，以二进制封装得到数据，具有通用性  
                    byte[] data = readInputStream(inStream);  
                    System.out.println(new String(data));
                    //new一个文件对象用来保存图片，默认保存当前工程根目录  
                    File imageFile = new File("D:\\"+name+".jpg");
                    //创建输出流  
                    FileOutputStream outStream = new FileOutputStream(imageFile);  
                    //写入数据  
                    outStream.write(data);  
                    //关闭输出流  
                    outStream.close(); 
                }
            }
        }
    }
    
    public static byte[] readInputStream(InputStream inStream) throws Exception{  
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();  
        //创建一个Buffer字符串  
        byte[] buffer = new byte[1024];  
        //每次读取的字符串长度，如果为-1，代表全部读取完毕  
        int len = 0;  
        //使用一个输入流从buffer里把数据读取出来  
        while( (len=inStream.read(buffer)) != -1 ){  
            //用输出流往buffer里写入数据，中间参数代表从哪个位置开始读，len代表读取的长度  
            outStream.write(buffer, 0, len);  
        }  
        //关闭输入流  
        inStream.close();  
        //把outStream里的数据写入内存  
        return outStream.toByteArray();  
    }  
    
}
