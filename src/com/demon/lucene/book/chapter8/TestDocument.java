package com.demon.lucene.book.chapter8;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse.ShardInfo;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;

/**
 * 文档管理
 * 
 * @author xuliang
 * @since 2019年8月29日 上午10:33:15
 *
 */
public class TestDocument {

    private RestHighLevelClient restClient = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.11.248", 9200, "http")));
    private RequestOptions REQUEST_OPTIONS_DEFAULT = RequestOptions.DEFAULT;
    
    /**
     * 索引文档
     */
    @Test
    public void createDoc() throws IOException{
        IndexRequest request = new IndexRequest("twitter");
        // json 字符串
        String jsonString = "{" +
                        "\"user\":\"kimchy\"," +
                        "\"postDate\":\"2019-08-29\"," +
                        "\"message\":\"trying out Elasticsearch\"" +
                        "}";
        request.id("1").source(jsonString, XContentType.JSON);
        IndexResponse jsonResp = restClient.index(request, REQUEST_OPTIONS_DEFAULT);
        System.out.println("index:" + jsonResp.getIndex());
        System.out.println("id: " + jsonResp.getId());
        if(jsonResp.getResult() == DocWriteResponse.Result.CREATED){
            // 新建文档
            System.out.println("doc CREATED");
        }else if(jsonResp.getResult() == DocWriteResponse.Result.UPDATED){
            // 更新文档
            System.out.println("doc UPDATED");
        }
        ShardInfo shardInfo = jsonResp.getShardInfo();
        System.out.println("total shard: " + shardInfo.getTotal() + " success shard: " + shardInfo.getSuccessful());
        
        // map 
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("user", "map");
        jsonMap.put("postDate", "2019-08-29");
        jsonMap.put("message", "index map ES");
        request.id("2").source(jsonMap);
        IndexResponse mapResp = restClient.index(request, REQUEST_OPTIONS_DEFAULT);
        System.out.println("index map, id:" + mapResp.getId() + " result:" + mapResp.getResult());
        
        // XContent
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("user", "xcontent");
            builder.timeField("postDate", "2019-08-29");
            builder.field("message", "index xcontent ES");
        }
        builder.endObject();
        request.id("3").source(builder);
        IndexResponse xcontentResp = restClient.index(request, REQUEST_OPTIONS_DEFAULT);
        System.out.println("index xcontent, id:" + xcontentResp.getId() + " result:" + xcontentResp.getResult());
        
        // key-value
        request.id("4").source("user", "key-value", "postDate", "2019-08-29", "message", "index key-value ES");
        IndexResponse kvResp = restClient.index(request, REQUEST_OPTIONS_DEFAULT);
        System.out.println("index key-value, id:" + kvResp.getId() + " result:" + kvResp.getResult());
    }
    
    /**
     * 获取文档
     */
    @Test
    public void getDoc() throws IOException{
        GetRequest getRequest = new GetRequest("twitter", "1");
        GetResponse getResponse = restClient.get(getRequest, REQUEST_OPTIONS_DEFAULT);
        System.out.println(getResponse.getSourceAsString());
        System.out.println(getResponse.getVersion());
    }
    
    /**
     * 删除文档
     */
    @Test
    public void delDoc() throws IOException{
        DeleteRequest request = new DeleteRequest("twitter", "1");
        DeleteResponse resp = restClient.delete(request, REQUEST_OPTIONS_DEFAULT);
        System.out.println(resp.getIndex());
        System.out.println(resp.getId());
        ShardInfo shardInfo = resp.getShardInfo();
        System.out.println(shardInfo.getTotal() + " - " + shardInfo.getSuccessful());
    }
    
}
