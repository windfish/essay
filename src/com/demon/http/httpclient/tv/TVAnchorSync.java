package com.demon.http.httpclient.tv;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * 
 * @author xuliang
 * @since 2019年3月6日 下午1:48:01
 *
 */
public class TVAnchorSync {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final Charset defaultCharset = Charset.forName("UTF-8");
    private CloseableHttpClient client = HttpClients.createDefault();
    private static final RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(5000)    // 设置连接超时时间
            .setConnectionRequestTimeout(1000)  // 设置从connect Manager(连接池)获取Connection 超时时间
            .setSocketTimeout(5000)     // 设置获取数据的超时时间
            .build();
    
    private HttpGet httpGet(String uri){
        HttpGet httpGet = new HttpGet(uri);
        httpGet.setConfig(requestConfig);
        return httpGet;
    }
    
    private HttpPost httpPost(String uri){
        HttpPost httpPost = new HttpPost(uri);
        httpPost.setConfig(requestConfig);
        return httpPost;
    }
    
    // cate_id 直播平台分类     本地分类
 // ("0", "101030");//热门
//    ("1", "10010300");// 畅聊
//    ("2", "10010500");// 舞蹈
//    ("3", "10010600");// 歌唱
//    ("4", "101040");// 特色
//    ("5", "10010400");// 天籁
//    ("15", "10010500");// 乐器
    public void tv9xiuSync(){
        String url = "http://www.9xiu.com/interface/liveRoom/liveTaShan?cate_id=0&source=tashan&page=1&pagesize=10";
        try{
            CloseableHttpResponse response = client.execute(httpGet(url));
            if(response != null){
                HttpEntity entity = response.getEntity();
                if(entity != null){
                    String result = EntityUtils.toString(entity, defaultCharset);
                    logger.info("9xiu result: {}", result);
                }
            }
        }catch (Exception e) {
            logger.error("9xiu sync exception", e);
        }
    }
    
    // cate_id 直播平台分类     本地分类
//    ("a431", "101040");// 手机达人
//    ("b26", "10010400");// 新秀
//    ("c421", "101030");// 女神
//    ("d421", "10010600");// 女神
//    ("e31", "10010200");// 好声音
//    ("d120", "10010500");// 热舞
//    ("g156", "10010400");// 搞笑
//    ("h961", "20021860");// 狼人杀
//    ("i966", "10010200");// 音乐现场
    public void tvkkSync(){
        try{
            String url = "https://sapikg.kktv1.com/kkgame/entrance?parameter=";
            
            JSONObject param = new JSONObject();
            param.put("cataId", "421");
            param.put("count", "10");
            param.put("FuncTag", "20010309");
            param.put("platform", "1");
            param.put("a", "2");
            param.put("c", "100101");
            url += URLEncoder.encode(param.toJSONString(), defaultCharset.name());
            
            CloseableHttpResponse response = client.execute(httpGet(url));
            if(response != null){
                HttpEntity entity = response.getEntity();
                if(entity != null){
                    String result = EntityUtils.toString(entity, defaultCharset);
                    logger.info("kk result: {}", result);
                }
            }
        }catch (Exception e) {
            logger.error("kk sync exception", e);
        }
    }
    
    public void tvKuaishouSync(){
        try{
            String url = "https://www.kuaishouvideo.com/Tashan";
            
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("key", "osd9n6u9oyqte3q35dmnul030y0b49g2"));
            params.add(new BasicNameValuePair("mid", ""));
            
            HttpPost httpPost = httpPost(url);
            httpPost.setEntity(new UrlEncodedFormEntity(params, defaultCharset));
            
            CloseableHttpResponse response = client.execute(httpPost);
            if(response != null){
                HttpEntity entity = response.getEntity();
                if(entity != null){
                    String result = EntityUtils.toString(entity, defaultCharset);
                    logger.info("kuaishou result: {}", result);
                }
            }
            
        }catch (Exception e) {
            logger.error("kuaishou sync exception", e);
        }
    }
    
    public static void main(String[] args) {
//        new TVAnchorSync().tv9xiuSync();
//        new TVAnchorSync().tvkkSync();
        new TVAnchorSync().tvKuaishouSync();
    }
    
}
