package com.demon.http.httpclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.MessageFormat;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件上传
 * 例子：微信小程序临时素材上传
 * 
 * @author xuliang
 * @since 2018年9月4日 上午11:08:13
 *
 */
public class FileHttpTest {

    private static Logger logger = LoggerFactory.getLogger(FileHttpTest.class);
    private static String url = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token={0}&type=image";
    
    public static void main(String[] args) {
        try{
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost post = new HttpPost(MessageFormat.format(url, "wx_access_token"));
            FileBody file = new FileBody(new File("qrCode.jpg"));
            HttpEntity reqEntity = MultipartEntityBuilder.create().addPart("file", file).build();
            post.setEntity(reqEntity);
            CloseableHttpResponse response = httpClient.execute(post);
            StringBuilder sb = new StringBuilder();
            if(response != null){
                HttpEntity entity = response.getEntity();
                if(entity != null){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                }
            }
            String resp = sb.toString();
            logger.info(resp);
            
        }catch (Exception e) {
            logger.error("upload wx image exception", e);
        }
    }
    
}
