package com.demon.http.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.demon.util.MD5Utils;


public class HttpTest {

    public static void main(String[] args) throws Exception {

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("channelOrderId", "2018051121001004720525018210"));
        params.add(new BasicNameValuePair("gmtCreate", "2018-05-11 17:14:10"));
        params.add(new BasicNameValuePair("gmtPayment", "2018-05-11 17:14:33"));
        params.add(new BasicNameValuePair("inOrderId", "SP20180511171410432gvSMSLjxYS"));
        params.add(new BasicNameValuePair("mchId", "C2018042800182"));
        params.add(new BasicNameValuePair("mchOrderId", "20180511110028"));
        params.add(new BasicNameValuePair("orderAmt", "0.01"));
        params.add(new BasicNameValuePair("payChannel", "alipay"));
        params.add(new BasicNameValuePair("prodDesc", URLEncoder.encode("充值")));
        params.add(new BasicNameValuePair("prodName", URLEncoder.encode("充值")));
        params.add(new BasicNameValuePair("tradeStatus", "TRADE_SUCCESS"));
        params.add(new BasicNameValuePair("signType", "RSA"));
        params.add(new BasicNameValuePair("signMsg", "87643ADC9B145FAC268976B7B755248BCBC4AD317698A1FD61B8EE697F9CC34FD307E435BDCC6B3FEDD6E60E3F4EF67A4E4A9977D723977F4D6412714E9AC29BFDBDA1A377383130AC5FF23C4F4A0F39F29880D4470E5A6DA4D432DCFE1E348BDA6AA1132FA189E913CD7755B6581D78EBACAECD421F8C7A93667A40819A070B"));
        
        UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(params);
        HttpPost httpPost = new HttpPost("http://money.uuuwin.com/api/pay/sevenpayCallback");
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
