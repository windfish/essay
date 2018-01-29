package com.demon.http.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicNameValuePair;

import com.alibaba.fastjson.JSON;

/**
 * HttpClient 基础类和方法
 * @author xuliang
 * @since 2018年1月29日 下午2:42:12
 *
 */
public class MainTest {

    public static void main(String[] args) throws Exception {
        /*
         * Http 请求
         * HttpClient支持开箱即用HTTP/1.1规范中定义的所有HTTP方法：GET, HEAD,POST, PUT, DELETE,TRACE and OPTIONS
         * 它们都有一个特定的类对应这些方法类型: HttpGet,HttpHead, HttpPost,HttpPut, HttpDelete,HttpTrace, and HttpOptions
         * 
         * URIBuilder 实用类来简化请求 URL的创建和修改
         */
        URI uri = new URIBuilder()
                .setScheme("http")
                .setHost("www.baidu.com")
                .setPath("/s")
                .setParameter("ie", "UTF-8")
                .setParameter("f", "8")
                .setParameter("rsv_bp", "1")
                .setParameter("rsv_idx", "2")
                .setParameter("tn", "baiduhome_pg")
                .setParameter("wd", "httpclient")
                .setParameter("rsv_spt", "1")
                .setParameter("og", "httpclient")
                .setParameter("rsv_pq", "98812c4300014582")
                .setParameter("rsv_t", "a639bfAHWMurw1IqGReOaHOeDTkhua2q2YzZ0XgNUD3sN3lGno4RrWa2nxT%2BP6xUq%2BC3")
                .setParameter("rqlang", "cn")
                .setParameter("rsv_enter", "0")
                .build();
        HttpGet httpGet = new HttpGet(uri);
        System.out.println(httpGet.getURI());
        System.out.println("-----------------------------------------");
        
        /*
         * Http 响应
         * HTTP响应是服务器端在接收和解释客户端请求消息后，返回客户端的消息。
         * 该消息的第一行包含协议版本以及后面跟着的数字形式的状态代码和相关的文本段。
         */
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");
        System.out.println(response.getProtocolVersion());
        System.out.println(response.getStatusLine().getStatusCode());
        System.out.println(response.getStatusLine().getReasonPhrase());
        System.out.println(response.getStatusLine().toString());
        System.out.println("-----------------------------------------");
        
        /*
         * 消息头
         * 消息头部可以包含描述信息，例如：内容长度，内容类型等。
         */
        response.addHeader("Set-Cookie", "c1=a; path=/; domain=localhost");
        response.addHeader("Set-Cookie", "c2=b; path=\"/\", c3=c; domain=\"localhost\"");
        Header h1 = response.getFirstHeader("Set-Cookie");
        System.out.println(h1);
        Header h2 = response.getLastHeader("Set-Cookie");
        System.out.println(h2);
        Header[] hs = response.getHeaders("Set-Cookie");
        System.out.println(hs.length);
        
        // 获取所有头部信息
        System.out.println("--------获取所有头部信息");
        response.addHeader("test-header", "test");
        HeaderIterator it = response.headerIterator();
        while(it.hasNext()){
            System.out.println(it.next());
        }
        
        // 头部信息解析为参数
        System.out.println("--------解析头部信息");
        HeaderElementIterator eit = new BasicHeaderElementIterator(response.headerIterator("Set-Cookie"));
        while(eit.hasNext()){
            HeaderElement elem = eit.nextElement();
            System.out.println(elem.getName()+" = "+elem.getValue());
            NameValuePair[] params = elem.getParameters();
            for(int i=0;i<params.length;i++){
                System.out.println(" "+params[i]);
            }
        }
        System.out.println("-----------------------------------------");
        
        /*
         * HTML 表单
         */
        List<NameValuePair> params = new ArrayList<>();
        Map<String, String> map = new HashMap<>();
        map.put("gameId", "G10");
        params.add(new BasicNameValuePair("data", JSON.toJSONString(map)));
        params.add(new BasicNameValuePair("time", "1517206164804"));
        params.add(new BasicNameValuePair("sessionid", "8f64859b5e864f8cb3a20865feef895fwb"));
        params.add(new BasicNameValuePair("sign", "bd774e625aca52f9236a160b5ee63f47"));
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params);
        HttpPost httpPost = new HttpPost("http://pk.uuuwin.com/api/dnf/listDnfServer");
        httpPost.setEntity(urlEncodedFormEntity);
        RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectTimeout(5000)    // 设置连接超时时间
                    .setConnectionRequestTimeout(1000)  // 设置从connect Manager(连接池)获取Connection 超时时间
                    .setSocketTimeout(5000)     // 设置获取数据的超时时间
                    .build();
        httpPost.setConfig(requestConfig);
        
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse resp = client.execute(httpPost);
        if(resp != null){
            HttpEntity entity = resp.getEntity();
            if(entity != null){
                BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                try {
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    System.out.println(sb.toString());
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
        System.out.println("-----------------------------------------");
        
    }
    
}
