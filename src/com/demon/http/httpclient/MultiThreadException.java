package com.demon.http.httpclient;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

/**
 * 多线程公用一个HttpClient 对象，一个请求未结算，再次发起一个新请求时，会出现异常：
 * java.lang.IllegalStateException: Invalid use of BasicClientConnManager: connection still allocated.
 * Make sure to release the connection before allocating another one.
 * 
 * @author xuliang
 * @since 2018年12月20日 下午3:42:18
 *
 */
@SuppressWarnings("deprecation")
public class MultiThreadException {

    /**
     * 低版本的HttpClient，使用ThreadSafeClientConnManager 来替代BasicClientConnManager
     */
    public CloseableHttpClient getHttpClientOldVersion(){
        BasicHttpParams httpParams = new BasicHttpParams();  
        HttpConnectionParams.setConnectionTimeout(httpParams, 3000);  
        HttpConnectionParams.setSoTimeout(httpParams, 5000);
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
        cm.setMaxTotal(20);
        CloseableHttpClient client = new DefaultHttpClient(cm, httpParams);
        return client;
    }
    
    /**
     * 高版本的HttpClient，使用PoolingHttpClientConnectionManager
     * @return
     */
    public CloseableHttpClient getHttpClientNewVersion(){
        PoolingHttpClientConnectionManager pcm = new PoolingHttpClientConnectionManager();
        pcm.setMaxTotal(20);
        CloseableHttpClient client = HttpClients.custom().setConnectionManager(pcm).build();
        return client;
    }
    
}
