package com.demon.kyfw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 百度文字、图片识别
 * 
 * @author xuliang
 * @since 2018年9月7日 下午3:14:29
 *
 */
public class BaiduOcrApi {
    
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final String API_Key = "k2su7Ek2yD6HGqDCuh2OCUyU";
    private final String Secret_Key = "bfyAWYyc9RdSc8ZoUegx2dPYkjHzscmI";
    private final String Access_Token = "24.7993cc4cfbeed14d852346c987f00693.2592000.1544150854.282335-10639410";
    
    private CloseableHttpClient httpClient = HttpClients.createDefault();
    
    public void getAccessToken(){
        String url = "https://aip.baidubce.com/oauth/2.0/token?grant_type=client_credentials&client_id="+API_Key+"&client_secret="+Secret_Key;
        String accessToken = get(url, null);
        logger.info(accessToken);
    }
    
    public void wordRecognition(String url){
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理  
        InputStream in = null;  
        byte[] data = null;  
        //读取图片字节数组  
        try   
        {  
            in = new FileInputStream(new File(url));          
            data = new byte[in.available()];  
            in.read(data);  
            in.close();  
        }   
        catch (IOException e)   
        {  
            e.printStackTrace();  
        }  
        //对字节数组Base64编码  
        String imgData = Base64.getEncoder().encodeToString(data);//返回Base64编码过的字节数组字符串
        logger.info("img data:{}", imgData);
        
        Map<String, String> params = new HashMap<>();
        params.put("image", imgData);
        String post = post("https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic?access_token="+Access_Token, params);
        logger.info("word recognition result:{}", post);
        if(post == null){
            return ;
        }
//        JSONObject json = JSON.parseObject(post);
//        json.getJSONArray("words_result").getst
    }
    
    public void imageRecognition(String url) {
        //将图片文件转化为字节数组字符串，并对其进行Base64编码处理  
        InputStream in = null;  
        byte[] data = null;  
        //读取图片字节数组  
        try   
        {  
            in = new FileInputStream(new File(url));          
            data = new byte[in.available()];  
            in.read(data);  
            in.close();  
        }   
        catch (IOException e)   
        {  
            e.printStackTrace();  
        }  
        //对字节数组Base64编码  
        String imgData = Base64.getEncoder().encodeToString(data);//返回Base64编码过的字节数组字符串
        logger.info("img data:{}", imgData);
        
        Map<String, String> params = new HashMap<>();
        params.put("image", imgData);
        String post = post("https://aip.baidubce.com/rest/2.0/image-classify/v2/advanced_general?access_token="+Access_Token, params);
        logger.info("image recognition result:{}", post);
        if(post == null){
            return ;
        }
//        JSONObject json = JSON.parseObject(post);
    }
    
    private String get(String url, Map<String, String> params){
        StringBuilder sb = new StringBuilder();
        try{
            URIBuilder builder = new URIBuilder(url);
            if(params != null){
                for(Map.Entry<String, String> entry: params.entrySet()){
                    builder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            HttpGet httpGet = new HttpGet(builder.build());
            CloseableHttpResponse resp = httpClient.execute(httpGet);
            if(resp != null){
                HttpEntity entity = resp.getEntity();
                if(entity != null){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String line = null;
                    try {
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                    } catch (IOException e) {
                    } finally {
                        try {
                            reader.close();
                        } catch (Exception e2) {
                        }
                        try {
                            entity.getContent().close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
            resp.close();
        }catch (Exception e) {
            logger.error("http get exception", e);
        }
        return sb.toString();
    }
    
    private String post(String url, Map<String, String> params){
        StringBuilder sb = new StringBuilder();
        try{
            List<NameValuePair> reqParams = new ArrayList<>();
            if(params != null){
                for(Map.Entry<String, String> entry: params.entrySet()){
                    reqParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(reqParams);
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httpPost.setEntity(encodedFormEntity);
            CloseableHttpResponse resp = httpClient.execute(httpPost);
            if(resp != null){
                HttpEntity entity = resp.getEntity();
                if(entity != null){
                    BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String line = null;
                    try {
                        while ((line = reader.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                    } catch (IOException e) {
                    } finally {
                        try {
                            reader.close();
                        } catch (Exception e2) {
                        }
                        try {
                            entity.getContent().close();
                        } catch (IOException e) {
                        }
                    }
                }
            }
            resp.close();
            
        }catch (Exception e) {
            logger.error("http post exception", e);
        }
        return sb.toString();
    }
    
    public static void main(String[] args) {
//        new BaiduOcrApi().getAccessToken();
//        new BaiduOcrApi().wordRecognition("D:\\img\\0.jpg");
//        new BaiduOcrApi().imageRecognition("D:\\img\\6.jpg");
        new BaiduOcrApi().imageRecognition("D:\\img\\1.jpg");
    }
    
}
