package com.demon.lucene.book.chapter8;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

/**
 * 利用传输机TransportClient 创建client 对象链接到ES 集群，这种方式只会连接到集群并不会加入集群
 * 
 * @author xuliang
 * @since 2019年8月28日 上午10:14:40
 *
 */
@SuppressWarnings({ "deprecation", "resource" })
public class TestClient {

    private static String CLUSTER_NAME = "elasticsearch";   // 集群名称
    private static String HOST_IP = "192.168.11.248";   // 服务器IP
    private static int TCP_PORT = 9300;     // 端口
    
    /**
     * 7.2 RestHighLevelClient 客户端
     */
    @Test
    public void testRestClient() throws IOException{
        RestHighLevelClient client = new RestHighLevelClient(RestClient.builder(new HttpHost(HOST_IP, 9200, "http")));
        GetRequest getRequest = new GetRequest("books", "1");
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);
        System.out.println(getResponse.getSourceAsString());
    }
    
    /**
     * TransportClient 在7.0.0中弃用，并将在8.0被删除
     * 
     * TransportClient 需要与Elasticsearch 实例的版本一致，否则会导致兼容性问题
     * 7.2.1 依赖的lucene 版本为8.0.0，依赖的netty 版本为4.1.35.Final，依赖的joda-time 版本为2.10.2 
     */
    @Test
    public void testTransportClient() throws UnknownHostException{
        Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
        TransportClient client = new PreBuiltTransportClient(settings)
                                .addTransportAddress(new TransportAddress(InetAddress.getByName(HOST_IP), TCP_PORT));
        GetResponse getResponse = client.prepareGet("books", "IT", "1").get();
        System.out.println(getResponse.getSourceAsString());
    }
    
}
