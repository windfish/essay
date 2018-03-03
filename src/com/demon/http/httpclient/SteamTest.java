package com.demon.http.httpclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author xuliang
 * @since 2018年1月30日 上午10:14:56
 *
 */
public class SteamTest {
    
    private static Logger logger = LoggerFactory.getLogger(SteamTest.class);

    public static void main(String[] args) throws Exception {
//        testRegex();
        testInventory();
//        testGetrsakey();
        System.out.println(System.currentTimeMillis());
    }
    
    private static void testGetrsakey() throws Exception{
        HttpPost httpPost = new HttpPost("https://steamcommunity.com/login/getrsakey");
        httpPost.addHeader("Accept", "application/json, text/javascript;q=0.9, */*;q=0.5");
        httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.57 Safari/537.36");
        // post.addHeader("Host", "steamcommunity.com");
        httpPost.addHeader("Referer", "https://steamcommunity.com/login/home/?goto=");
        
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
        nvps.add(new BasicNameValuePair("username", "mfh2064078"));  
        nvps.add(new BasicNameValuePair("donotcache", "" + System.currentTimeMillis()));  
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(httpPost);
        System.out.println(response);
        if(response != null){
            HttpEntity entity = response.getEntity();
            System.out.println(entity);
            if(entity != null){
                String result = streamToString(entity.getContent());
                System.out.println(result);
            }
        }
    }
    
    private static void testInventory() throws Exception{
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)    // 设置连接超时时间
                .setConnectionRequestTimeout(1000)  // 设置从connect Manager(连接池)获取Connection 超时时间
                .setSocketTimeout(5000)     // 设置获取数据的超时时间
                .build();
        // 76561198433042770
        // http://steamcommunity.com/steamcommunity.com/inventory/76561198433042770
        // https://steamcommunity.com/profiles/76561198433042770/inventory/
//        HttpGet httpGet = new HttpGet("http://47.254.29.54:8004/steamcommunity.com/profiles/76561198389582507/inventory/json/570/2/?trading=1");
//        System.out.println(httpGet.getURI());
//        httpGet.setConfig(requestConfig);
        HttpPost httpPost = new HttpPost("http://47.254.29.54:8004/steamcommunity.com/profiles/76561198389582507/inventory/json/570/2/?trading=1");
        httpPost.setConfig(requestConfig);
        
        Set<String> set = new TreeSet<>();
        
        PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager();
        pcm.setMaxTotal(20);
        
//        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(pcm).build();
        CloseableHttpResponse response = client.execute(httpPost);
        System.out.println(response);
        if(response != null){
            HttpEntity entity = response.getEntity();
            System.out.println(entity);
            if(entity != null){
                String result = streamToString(entity.getContent());
                JSONObject jsonObject = JSONObject.parseObject(result);
                JSONObject rgInventory = jsonObject.getJSONObject("rgInventory");
                
                rgInventory.keySet().stream().forEach(e -> {
                    System.out.println(e);
                    set.add(e);
                });
            }
        }
        
    }
    
    private static void testRegex(){
        String str = "<script type=\"text/javascript\">"
                +"var g_rgAppContextData = {\"570\":{\"appid\":570,\"name\":\"Dota 2\",\"icon\":\"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/apps/570/0bbb630d63262dd66d2fdd0f7d37e8661a410075.jpg\",\"link\":\"http://steamcommunity.com/app/570\",\"asset_count\":18,\"inventory_logo\":\"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/apps/570/910ef16cd7bf6c6986e78b3ad4eee7eaa5d26cc0.png\",\"trade_permissions\":\"FULL\",\"load_failed\":0,\"rgContexts\":{\"2\":{\"asset_count\":18,\"id\":\"2\",\"name\":\"Backpack\"}}},\"578080\":{\"appid\":578080,\"name\":\"PLAYERUNKNOWN'S BATTLEGROUNDS\",\"icon\":\"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/apps/578080/93d896e7d7a42ae35c1d77239430e1d90bc82cae.jpg\",\"link\":\"http://steamcommunity.com/app/578080\",\"asset_count\":7,\"trade_permissions\":\"FULL\",\"load_failed\":0,\"rgContexts\":{\"2\":{\"asset_count\":7,\"id\":\"2\",\"name\":\"Backpack\"}}},\"230410\":{\"appid\":230410,\"name\":\"Warframe\",\"icon\":\"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/apps/230410/bcf887d7b0707e5418e1680de92e85ca899516ac.jpg\",\"link\":\"http://steamcommunity.com/app/230410\",\"asset_count\":0,\"inventory_logo\":\"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/apps/230410/49958ed9dfdee9545369e41778c9a986be5bbfa8.png\",\"trade_permissions\":\"FULL\",\"load_failed\":0,\"rgContexts\":{\"2\":{\"asset_count\":0,\"id\":\"2\",\"name\":\"Inventory\"}}},\"753\":{\"appid\":753,\"name\":\"Steam\",\"icon\":\"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/apps/753/135dc1ac1cd9763dfc8ad52f4e880d2ac058a36c.jpg\",\"link\":\"http://steamcommunity.com/app/753\",\"asset_count\":0,\"inventory_logo\":\"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/apps/753/db8ca9e130b7b37685ab2229bf5a288aefc3f0fa.png\",\"trade_permissions\":\"FULL\",\"load_failed\":0,\"rgContexts\":{\"1\":{\"asset_count\":0,\"id\":\"1\",\"name\":\"\u793c\u7269\"},\"3\":{\"asset_count\":0,\"id\":\"3\",\"name\":\"\u4f18\u60e0\u5238\"},\"6\":{\"asset_count\":0,\"id\":\"6\",\"name\":\"\u793e\u533a\"},\"7\":{\"asset_count\":0,\"id\":\"7\",\"name\":\"\u7269\u54c1\u5956\u52b1\"}}}};"
                +"var g_rgPartnerAppContextData = {\"578080\":{\"appid\":578080,\"name\":\"PLAYERUNKNOWN'S BATTLEGROUNDS\",\"icon\":\"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/apps/578080/93d896e7d7a42ae35c1d77239430e1d90bc82cae.jpg\",\"link\":\"http://steamcommunity.com/app/578080\",\"asset_count\":73,\"trade_permissions\":\"FULL\",\"load_failed\":0,\"rgContexts\":{\"2\":{\"asset_count\":73,\"id\":\"2\",\"name\":\"Backpack\"}}},\"753\":{\"appid\":753,\"name\":\"Steam\",\"icon\":\"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/apps/753/135dc1ac1cd9763dfc8ad52f4e880d2ac058a36c.jpg\",\"link\":\"http://steamcommunity.com/app/753\",\"asset_count\":0,\"inventory_logo\":\"https://steamcdn-a.akamaihd.net/steamcommunity/public/images/apps/753/db8ca9e130b7b37685ab2229bf5a288aefc3f0fa.png\",\"trade_permissions\":\"FULL\",\"load_failed\":0,\"rgContexts\":{\"3\":{\"asset_count\":0,\"id\":\"3\",\"name\":\"\u4f18\u60e0\u5238\"},\"6\":{\"asset_count\":0,\"id\":\"6\",\"name\":\"\u793e\u533a\"},\"7\":{\"asset_count\":0,\"id\":\"7\",\"name\":\"\u7269\u54c1\u5956\u52b1\"}}}};"
                +"var g_rgForeignAppContextData = { };"
                +"var g_rgWalletInfo = {\"success\":false};"
                +"var g_strCountryCode = \"CN\";"
                +"var g_bMarketAllowed = true;"
                +"var g_ulTradePartnerSteamID = '76561198433042770';"
                +"var g_bTradePartnerProbation = false;"
                +"var g_sessionID = \"9f1a73656645b73fc7092286\";"
                +""
                +"var g_rgCurrentTradeStatus = {\"newversion\":true,\"version\":1,\"me\":{\"assets\":[],\"currency\":[],\"ready\":false},\"them\":{\"assets\":[],\"currency\":[],\"ready\":false}};"
                +"var g_strTradePartnerPersonaName = \"fisherXeon\";";

        String partner = "";
        String username = "";
        
        String regex = "var g_ulTradePartnerSteamID = '(?<partner>.*?)'";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            partner = matcher.group("partner");
        }
        String regexUsername = "var g_strTradePartnerPersonaName = \"(?<username>.*?)\"";
        pattern = Pattern.compile(regexUsername);
        matcher = pattern.matcher(str);
        if (matcher.find()) {
            username = matcher.group("username");
        }
        logger.info("partnerSteamId:{} username:{}", partner, username);
    }
    
    private static void testHttpClient() throws Exception {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5000)    // 设置连接超时时间
                .setConnectionRequestTimeout(1000)  // 设置从connect Manager(连接池)获取Connection 超时时间
                .setSocketTimeout(5000)     // 设置获取数据的超时时间
                .build();
        // 76561198433042770
        // http://steamcommunity.com/steamcommunity.com/inventory/76561198433042770
        // https://steamcommunity.com/profiles/76561198433042770/inventory/
        HttpGet httpGet = new HttpGet("http://47.254.29.54:8004/steamcommunity.com/tradeoffer/new/?partner=249010092&token=zYpFS9nN");
        System.out.println(httpGet.getURI());
        httpGet.setConfig(requestConfig);
        
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(httpGet);
        System.out.println(response);
        if(response != null){
            HttpEntity entity = response.getEntity();
            System.out.println(entity);
            if(entity != null){
                String str = streamToString(entity.getContent());
                System.out.println(str);
            }
        }
    }
    
    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
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
                is.close();
            } catch (IOException e) {
            }
        }
        return sb.toString();
    }
    
}
