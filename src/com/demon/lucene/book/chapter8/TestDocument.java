package com.demon.lucene.book.chapter8;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BackoffPolicy;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.get.MultiGetItemResponse;
import org.elasticsearch.action.get.MultiGetRequest;
import org.elasticsearch.action.get.MultiGetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse.ShardInfo;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.ByteSizeUnit;
import org.elasticsearch.common.unit.ByteSizeValue;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.junit.Assert;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

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
    
    /**
     * 修改文档
     */
    @Test
    public void updateDoc() throws IOException{
        UpdateRequest request = new UpdateRequest("twitter", "1");
        // 脚本方式
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("count", 4);
        Script inline = new Script(ScriptType.INLINE, "painless", "ctx._source.count = params.count", parameters);
        request.script(inline);
        UpdateResponse update = restClient.update(request, REQUEST_OPTIONS_DEFAULT);
        System.out.println(update.getId() + " : " + update.getVersion());
        
        // upsert 文档存在，则更新；文档不存在，则新增
        UpdateRequest request2 = new UpdateRequest("twitter", "5").doc("updateTime", new Date(), "reason", "daily update").upsert("create", new Date());
        UpdateResponse update2 = restClient.update(request2, REQUEST_OPTIONS_DEFAULT);
        System.out.println(update2.getId() + " : " + update2.getVersion());
    }
    
    /**
     * 查询删除
     */
    @Test
    public void deleteByQuery() throws IOException{
        DeleteByQueryRequest request = new DeleteByQueryRequest("twitter");
        request.setQuery(new TermQueryBuilder("_id", 5));
        BulkByScrollResponse deleteByQuery = restClient.deleteByQuery(request, REQUEST_OPTIONS_DEFAULT);
        System.out.println(deleteByQuery.getTotal() + " : " + deleteByQuery.getDeleted());
    }
    
    /**
     * 批量获取
     */
    @Test
    public void multiGet() throws IOException{
        MultiGetRequest request = new MultiGetRequest();
        request.add(new MultiGetRequest.Item("twitter", "3"));
        request.add("books", "2");
        MultiGetResponse mget = restClient.mget(request, REQUEST_OPTIONS_DEFAULT);
        MultiGetItemResponse[] responses = mget.getResponses();
        for(MultiGetItemResponse resp: responses){
            Assert.assertNull(resp.getFailure());
            System.out.println(resp.getIndex() + " : " + resp.getId());
            GetResponse response = resp.getResponse();
            System.out.println(response.getVersion());
            System.out.println(response.getSourceAsString());
            System.out.println("-------------------");
        }
    }
    
    /**
     * 批量操作
     */
    @Test
    public void bulk() throws IOException{
        BulkRequest request = new BulkRequest();
        request.add(new IndexRequest("twitter").id("6").source(XContentType.JSON, "user", "bulk", "message", "add by bulk id 6", "postDate", new Date()));
        request.add(new IndexRequest("twitter").id("7").source("user", "bulk", "message", "add by bulk id 7", "postDate", new Date()));
        request.add(new IndexRequest("test").id("bulk").source("foo", "hello bulk"));
        BulkResponse bulk = restClient.bulk(request, REQUEST_OPTIONS_DEFAULT);
        for(BulkItemResponse response: bulk){
            DocWriteResponse resp = response.getResponse();
            
            switch(response.getOpType()){
            case INDEX:
                System.out.println(resp.getIndex() + " - " + resp.getId() + " - INDEX");
            case CREATE:
                System.out.println(resp.getIndex() + " - " + resp.getId() + " - CREATE");
                IndexResponse indexResponse = (IndexResponse) resp;
                System.out.println(JSON.toJSONString(indexResponse.getResult()));
                break;
            case UPDATE:
                System.out.println(resp.getIndex() + " - " + resp.getId() + " - UPDATE");
                UpdateResponse updateResponse = (UpdateResponse) resp;
                System.out.println(JSON.toJSONString(updateResponse.getResult()));
                break;
            case DELETE:
                System.out.println(resp.getIndex() + " - " + resp.getId() + " - DELETE");
                DeleteResponse deleteResponse = (DeleteResponse) resp;
                System.out.println(JSON.toJSONString(deleteResponse.getResult()));
                break;
            }
            System.out.println("----------------------------------");
        }
    }
    
    /**
     * bulk 批量操作支持在操作完成之前和之后进行相应的操作
     */
    @Test
    public void bulkProcessor() throws InterruptedException{
        BulkProcessor.Listener listener = new BulkProcessor.Listener() {
            
            @Override
            public void beforeBulk(long executionId, BulkRequest request) {
                // 批量操作提交之前执行
                System.out.println("before: " + executionId + " - " + JSON.toJSONString(request.requests()));
            }

            @Override
            public void afterBulk(long executionId, BulkRequest request, BulkResponse response) {
                // 批量操作提交之后执行
                System.out.println("after: " + executionId + " - " + request.numberOfActions());
            }
            
            @Override
            public void afterBulk(long executionId, BulkRequest request, Throwable failure) {
                // 批量操作异常时执行
                System.out.println("failure: " + executionId + " - " + request.toString());
            }
        };
        BulkProcessor bulkProcessor = BulkProcessor.builder(
                (request, bulkListener) -> restClient.bulkAsync(request, REQUEST_OPTIONS_DEFAULT, bulkListener), 
                listener)
                .setBulkActions(1)  // 请求数量多少次提交
                .setBulkSize(new ByteSizeValue(20, ByteSizeUnit.MB))    // 批处理请求达到20M 时提交
                .setFlushInterval(TimeValue.timeValueSeconds(10))    // 设置刷新索引的时间间隔
                .setConcurrentRequests(5)   // 设置并发处理线程个数
                .setBackoffPolicy(BackoffPolicy.exponentialBackoff(TimeValue.timeValueMillis(100), 3))  // 设置回滚策略，等待时间100ms，重试次数3次
                .build();
        
        IndexRequest one = new IndexRequest("twitter").id("9").source(XContentType.JSON, "user", "bulk", "message", "add by bulkProcessor id 9", "postDate", new Date());
        IndexRequest two = new IndexRequest("twitter").id("8").source("user", "bulk", "message", "add by bulkProcessor id 8", "postDate", new Date());
        IndexRequest three = new IndexRequest("test").id("bulkProcessor").source("foo", "hello bulkProcessor");
        bulkProcessor.add(one);
        bulkProcessor.add(two);
        bulkProcessor.add(three);
        
        boolean awaitClose = bulkProcessor.awaitClose(30L, TimeUnit.SECONDS);
        // true 所有批量请求都已完成；false 所有批量请求完成之前等待的时间已过
        System.out.println(awaitClose);
    }
    
}
