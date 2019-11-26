package com.demon.http.httpclient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

/**
 * HttpClient 工具类
 * @author xuliang
 * @since 2019年3月28日 下午4:35:03
 *
 */
public class BaseHttp {
    
    private static Logger logger = LoggerFactory.getLogger(BaseHttp.class);

    private static CloseableHttpClient client = null;
    static {
        PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager();
        pcm.setMaxTotal(50);
        client = HttpClients.custom().setConnectionManager(pcm).build();
    }
    
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)    // 设置连接超时时间
            .setConnectionRequestTimeout(1000)  // 设置从connect Manager(连接池)获取Connection 超时时间
            .setSocketTimeout(10000)     // 设置获取数据的超时时间
            .build();
    
    private static HttpGet httpGet(String uri){
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setConfig(requestConfig);
        return httpGet;
    }
    
    private static HttpPost httpPost(String uri){
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setConfig(requestConfig);
        return httpPost;
    }
    
    private static HttpPost httpPostWithHeaderMap(String uri, Map<String, String> headers){
        HttpPost httpPost = httpPost(uri);
        
        if(headers == null || headers.size() <= 0){
            return httpPost;
        }
        
        for(Map.Entry<String, String> entry: headers.entrySet()){
            httpPost.setHeader(entry.getKey(), entry.getValue());
        }
        
        return httpPost;
    }
    
    private static final Charset defaultCharset = Charset.forName("UTF-8");
    
    public static String get(String url){
        String responseText = "";
        CloseableHttpResponse response = null;
        try{
            response = client.execute(httpGet(url));
            if(response != null){
                HttpEntity entity = response.getEntity();
                if(entity != null){
                    responseText = EntityUtils.toString(entity, defaultCharset);
                    return responseText;
                }
            }
        }catch (Exception e) {
            logger.error("http get exception", e);
        } finally {
            try {
                if(response != null){
                    response.close();
                }
            } catch (Exception e) {
                logger.error("response.close exception:", e);
            }
        }
        return responseText;
    }
    
    public static String get(String url, Map<String, Object> params){
        return get(parseGetUrl(url, params));
    }
    
    private static String parseGetUrl(String url, Map<String, Object> paramMap) {
        StringBuilder sb = new StringBuilder(url);
        if (paramMap != null && paramMap.size() > 0) {
            String split = sb.indexOf("?") < 0 ? "?" : "&";
            for (Map.Entry<String, Object> param : paramMap.entrySet()) {
                String value = param.getValue() != null ? param.getValue().toString() : "";
                sb.append(split);
                split = "&";
                sb.append(param.getKey()).append("=").append(value);
            }
        }
        return sb.toString();
    }
    
    public static String post(String url, Map<String, Object> paramMap) {
        String responseText = "";
        try {
            List<NameValuePair> paramList = new ArrayList<>();
            if (paramMap != null) {
                for (Map.Entry<String, Object> param : paramMap.entrySet()) {
                    String value = param.getValue() != null ? param.getValue().toString() : "";
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), value);
                    paramList.add(pair);
                }
            }
            responseText = post(url, new UrlEncodedFormEntity(paramList, defaultCharset));
        } catch (Exception e) {
            logger.error("http post exception:", e);
        }
        return responseText;
    }
    
    public static String postWithJsonEntity(String url, Map<String, Object> paramMap) {
        String responseText = "";
        try {
            StringEntity requestEntity = null;
            if (paramMap != null) {
                requestEntity = new StringEntity(JSON.toJSONString(paramMap), ContentType.APPLICATION_JSON);
                requestEntity.setContentEncoding(defaultCharset.name());
                requestEntity.setContentType("application/json");
            }
            responseText = post(url, requestEntity);
        } catch (Exception e) {
            logger.error("http post exception:", e);
        }
        return responseText;
    }
    
    public static String postWithJsonEntity(String url, Map<String, String> paramMap, Map<String, String> headerMap) {
        String responseText = "";
        try {
            StringEntity requestEntity = null;
            if (paramMap != null) {
                requestEntity = new StringEntity(JSON.toJSONString(paramMap), ContentType.APPLICATION_JSON);
                requestEntity.setContentEncoding(defaultCharset.name());
                requestEntity.setContentType("application/json");
            }
            if(headerMap != null && !headerMap.isEmpty()){
                responseText = post(url, requestEntity, httpPostWithHeaderMap(url, headerMap));
            }else{
                responseText = post(url, requestEntity);
            }
        } catch (Exception e) {
            logger.error("http post exception:", e);
        }
        return responseText;
    }
    
    public static String parseParam2Str(Map<String, String> map) throws UnsupportedEncodingException{
        StringBuffer buffer = new StringBuffer();
        if (map != null && !map.isEmpty()) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                buffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), defaultCharset.name())).append("&");
            }
            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }
    
    private static String post(String url, HttpEntity requestEntity) {
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            HttpPost method = httpPost(url);
            if (requestEntity != null) {
                method.setEntity(requestEntity);
            }
            response = client.execute(method);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                responseText = EntityUtils.toString(responseEntity, defaultCharset);
            }
        } catch (Exception e) {
            logger.error("http post exception:", e);
        } finally {
            try {
                if(response != null){
                    response.close();
                }
            } catch (Exception e) {
                logger.error("response.close exception:", e);
            }
        }
        return responseText;
    }
    
    private static String post(String url, HttpEntity requestEntity, HttpPost method) {
        String responseText = "";
        CloseableHttpResponse response = null;
        try {
            if (requestEntity != null) {
                method.setEntity(requestEntity);
            }
            response = client.execute(method);
            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                responseText = EntityUtils.toString(responseEntity, defaultCharset);
            }
        } catch (Exception e) {
            logger.error("http post exception:", e);
        } finally {
            try {
                if(response != null){
                    response.close();
                }
            } catch (Exception e) {
                logger.error("response.close exception:", e);
            }
        }
        return responseText;
    }
    
}
