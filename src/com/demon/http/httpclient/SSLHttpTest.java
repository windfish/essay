package com.demon.http.httpclient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.demon.util.MD5;
import com.demon.util.XMLUtil;

/**
 * 带 CA证书的 HTTP 请求
 * 例子：微信公众号发红包
 * 
 * @author xuliang
 * @since 2018年9月4日 上午9:34:37
 *
 */
public class SSLHttpTest {

    private static Logger logger = LoggerFactory.getLogger(SSLHttpTest.class);
    
    private final static String MCH_ID = "test_mch_id";  // 商户号
    private final static String APP_ID = "test_app_id"; // 微信开发平台应用id
    private final static String API_KEY = "test_api_key"; // api key
    private final static String WX_REDPACK_URL = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
    
    public static void main(String[] args) {
        sendWxRedPack("oQ5inwP4P3hNsP4hYjiiMPCDB1-0", 100);
    }
    
    private static class BaseHttpClient {
        private static CloseableHttpClient httpClient = getHttpClientNewVersion();
        
        public static CloseableHttpClient getHttpClient(){
            return httpClient;
        }
        
    }
    
    /**
     * 高版本的 httpclient，SSLContexts 的包从  org.apache.http.conn.ssl 移到了 org.apache.http.ssl
     */
    private static CloseableHttpClient getHttpClientNewVersion(){
        try{
            KeyStore keyStore  = KeyStore.getInstance("PKCS12");
            FileInputStream instream = new FileInputStream(new File("apiclient_cert.p12")); //此处为证书所放的绝对路径
            
            try {
                keyStore.load(instream, MCH_ID.toCharArray());
            } finally {
                instream.close();
            }
    
            SSLContext sslcontext = org.apache.http.ssl.SSLContexts.custom()
                    .loadKeyMaterial(keyStore, MCH_ID.toCharArray())
                    .build();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();

            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connManager)
                    .build();
            return httpClient;
        }catch (Exception e) {
            logger.error("get httpclient new version exception", e);
            return null;
        }
    }
    
    /**
     * 低版本的 httpclient
     */
    @SuppressWarnings("deprecation")
    private static CloseableHttpClient getHttpClientOldVersion(){
        try{
            KeyStore keyStore  = KeyStore.getInstance("PKCS12");
            FileInputStream instream = new FileInputStream(new File("apiclient_cert.p12")); //此处为证书所放的绝对路径
            
            try {
                keyStore.load(instream, MCH_ID.toCharArray());
            } finally {
                instream.close();
            }
    
            // Trust own CA and all self-signed certs
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, MCH_ID.toCharArray())
                    .build();
            // Allow TLSv1 protocol only
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                    sslcontext,
                    new String[] { "TLSv1" },
                    null,
                    SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setSSLSocketFactory(sslsf)
                    .build();
            return httpClient;
        }catch (Exception e) {
            logger.error("get httpclient old version exception", e);
            return null;
        }
    }
    
    /**
     * 跳过证书验证，信任所有证书
     */
    private static CloseableHttpClient getHttpClientByTrustAllSSL(){
        try{
            SSLContext sslcontext = org.apache.http.ssl.SSLContexts.custom()
                    .loadTrustMaterial(new TrustStrategy() {
                        @Override
                        public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                            return true;
                        }
                    })
                    .build();
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", new SSLConnectionSocketFactory(sslcontext))
                    .build();

            PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connManager)
                    .build();
            return httpClient;
        }catch (Exception e) {
            logger.error("get httpclient by trust all ssl exception", e);
            return null;
        }
    }
    
    private static String sendWxRedPack(String openid, int amount){
        String mch_billno = "" + System.currentTimeMillis();
        
        SortedMap<String, String> params = new TreeMap<>();
        params.put("nonce_str", MD5.getInstance().encode(generateNonceStr()));
        params.put("mch_billno", mch_billno);
        params.put("mch_id", MCH_ID);
        params.put("wxappid", APP_ID);
        params.put("send_name", "test");
        params.put("re_openid", openid);
        params.put("total_amount", String.valueOf(amount));
        params.put("total_num", "1");
        params.put("wishing", "恭喜发财，大吉大利");
        params.put("client_ip", "122.224.145.14");
        params.put("act_name", "test");
        params.put("remark", "现金红包，快来抽奖");
        params.put("scene_id", "PRODUCT_2");
        params.put("sign", sign(params));
        
        String xml = "<xml>"
                + "<sign><![CDATA[{0}]]></sign>"
                + "<mch_billno><![CDATA[{1}]]></mch_billno>"
                + "<mch_id><![CDATA[{2}]]></mch_id>"
                + "<wxappid><![CDATA[{3}]]></wxappid>"
                + "<send_name><![CDATA[{4}]]></send_name>"
                + "<re_openid><![CDATA[{5}]]></re_openid>"
                + "<total_amount><![CDATA[{6}]]></total_amount>"
                + "<total_num><![CDATA[{7}]]></total_num>"
                + "<wishing><![CDATA[{8}]]></wishing>"
                + "<client_ip><![CDATA[{9}]]></client_ip>"
                + "<act_name><![CDATA[{10}]]></act_name>"
                + "<remark><![CDATA[{11}]]></remark>"
                + "<scene_id><![CDATA[{12}]]></scene_id>"
                + "<nonce_str><![CDATA[{13}]]></nonce_str>"
                + "</xml>";
        
        try {
            HttpPost httpPost = new HttpPost(WX_REDPACK_URL);
            StringEntity stringEntity = new StringEntity(MessageFormat.format(xml, 
                        params.get("sign"), params.get("mch_billno"), params.get("mch_id"), params.get("wxappid"), 
                        params.get("send_name"), params.get("re_openid"), params.get("total_amount"), params.get("total_num"), 
                        params.get("wishing"), params.get("client_ip"), params.get("act_name"), params.get("remark"), 
                        params.get("scene_id"), params.get("nonce_str")), "utf-8");
            httpPost.setEntity(stringEntity);
            CloseableHttpClient client = BaseHttpClient.getHttpClient();
            if(client == null){
                return "httpclient fail";
            }
            CloseableHttpResponse resp = client.execute(httpPost);
            String resXml = null;
            try{
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
                            resXml = sb.toString();
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
            }finally {
                resp.close();
            }
            logger.info("weixin redpack resXml:" + resXml);
        
            if(resXml == null || resXml.isEmpty()){
                return "send redpack fail";
            }
            
            @SuppressWarnings("rawtypes")
            Map map = XMLUtil.doXMLParse(resXml);
            Object returnCode = map.get("return_code");
            if("SUCCESS".equals(returnCode)) {
                Object resultCode = map.get("result_code");
                if("SUCCESS".equals(resultCode)) {
                    return "OK";
                }else{
                    Object errCode = map.get("err_code");
                    Object errCodeDes = map.get("err_code_des");
                    return String.valueOf(errCode) + String.valueOf(errCodeDes);
                }
            }else{
                Object returnMsg = map.get("return_msg");
                return String.valueOf(returnMsg);
            }
        } catch (Exception e) {
            logger.error("send redpack exception", e);
            return "send redpack fail";
        }
    }
    
    private static String generateNonceStr(){
        long currentTime = System.currentTimeMillis();
        int random = (int) (Math.random() * 10000);
        return "" + currentTime + random;
    }
    
    private static String sign(SortedMap<String, String> params){
        StringBuilder sb = new StringBuilder();
        for(Map.Entry<String, String> entry: params.entrySet()){
            if(entry.getValue() != null && !entry.getValue().isEmpty()){
                sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        sb.append("key=").append(API_KEY);
        return MD5.getInstance().encode(sb.toString()).toUpperCase();
    }
    
}
