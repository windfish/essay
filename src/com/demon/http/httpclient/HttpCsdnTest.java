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

/**
 * 防盗链的图片下载，设置 referer
 * 
 * @author xuliang
 * @since 2018年9月4日 上午11:08:28
 *
 */
public class HttpCsdnTest {

    public static void main(String[] args) throws Exception {
        
//        pullImgFromUrl("http://img3.laibafile.cn/p/s/303688780.png", "http://www.tianya.cn/", "tianya");
//        pullImgFromUrl("https://img-blog.csdnimg.cn/20190221172728151.gif", "https://blog.csdn.net", "csdn");
//        pullImgFromUrl("https://s1.51cto.com/images/blog/201902/15/a25e738b5139da642bffe241e6fb2769.png", "https://51cto.com", "51cto");
//        pullImgFromUrl("https://image-static.segmentfault.com/290/981/2909819103-5c6a7f9fcb461_articlex", "https://segmentfault.com", "segmentfault");
//        pullImgFromUrl("https://oscimg.oschina.net/oscnet/0b01f5ce7ff1628859099cef829779c7877.jpg", "https://my.oschina.net", "oschina");
//        pullImgFromUrl("https://note.youdao.com/yws/public/resource/7dd0d1c545c271096fd1cceaefac9ccb/xmlnote/365D4036E0F14213973F5E4A23927D23/3764", "http://youdao.com/", "youdao");
        pullImgFromUrl("https://ws3.sinaimg.cn/large/005BYqpggy1g0nax8c7sij30ka0e1qet.jpg", "", "sinaimg");
    }
    
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)    // 设置连接超时时间
            .setConnectionRequestTimeout(1000)  // 设置从connect Manager(连接池)获取Connection 超时时间
            .setSocketTimeout(5000)     // 设置获取数据的超时时间
            .build();
    
    public static void pullImgFromUrl(String url, String baseHost, String name) throws Exception{
        HttpGet httpGet = new HttpGet(url);
        System.out.println(httpGet.getURI());
        httpGet.setConfig(requestConfig);
        if(baseHost != null && !baseHost.trim().isEmpty()){
            httpGet.setHeader("referer", baseHost);
        }
        
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(httpGet);
        System.out.println(response);
        System.out.println("http code: " + response.getStatusLine().getStatusCode());
        System.out.println("content type: " + response.getHeaders("Content-Type")[0].getValue());
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
