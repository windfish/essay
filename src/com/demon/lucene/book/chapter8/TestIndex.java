package com.demon.lucene.book.chapter8;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.admin.indices.alias.Alias;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequest.AliasActions;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.close.CloseIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetMappingsRequest;
import org.elasticsearch.client.indices.GetMappingsResponse;
import org.elasticsearch.cluster.metadata.AliasMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;

import com.alibaba.fastjson.JSON;

/**
 * 索引管理
 * 
 * 版本原因，书中的代码已过期，参考官方文档
 * https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-indices-exists.html
 * 
 * @author xuliang
 * @since 2019年8月28日 下午2:35:09
 *
 */
public class TestIndex {

    private RestHighLevelClient restClient = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.11.248", 9200, "http")));
    private RequestOptions REQUEST_OPTIONS_DEFAULT = RequestOptions.DEFAULT;
    
    /**
     * 判断索引是否存在
     */
    @Test
    public void existsIndex() throws IOException{
        IndicesClient indices = restClient.indices();
        GetIndexRequest request = new GetIndexRequest("books");
        boolean exists = indices.exists(request, REQUEST_OPTIONS_DEFAULT);
        System.out.println(exists);
    }
    
    /**
     * 异步方式判断索引是否存在
     */
    @Test
    public void existsIndexAsync(){
        IndicesClient indices = restClient.indices();
        GetIndexRequest request = new GetIndexRequest("books");
        indices.existsAsync(request, REQUEST_OPTIONS_DEFAULT, 
                new ActionListener<Boolean>() {
                    @Override
                    public void onResponse(Boolean result) {
                        System.out.println(result);
                    }
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                    }
                });
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        System.out.println("end");
    }
    
    /**
     * 创建索引，也支持异步
     */
    @Test
    public void createIndex() throws IOException{
        CreateIndexRequest request = new CreateIndexRequest("twitter");
        // 设置settings
        request.settings(Settings.builder()
                .put("index.number_of_shards", 3)
                .put("index.number_of_replicas", 2)
                );
        
        // 设置mapping
        request.mapping(
                "{\n" +
                "  \"properties\": {\n" +
                "    \"message\": {\n" +
                "      \"type\": \"keyword\"\n" +
                "    }\n" +
                "  }\n" +
                "}", XContentType.JSON);
        // 还支持map 方式
        Map<String, Object> message = new HashMap<>();
        message.put("type", "keyword");
        Map<String, Object> properties = new HashMap<>();
        properties.put("message", message);
        Map<String, Object> mapping = new HashMap<>();
        mapping.put("properties", properties);
//        request.mapping(mapping);
        // 还支持XContentBuilder
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.startObject("properties");
            {
                builder.startObject("message");
                {
                    builder.field("type", "keyword");
                }
                builder.endObject();
            }
            builder.endObject();
        }
        builder.endObject();
//        request.mapping(builder);
        
        // 设置别名
        request.alias(new Alias("tw_alias"));
        
        CreateIndexResponse createResp = restClient.indices().create(request, REQUEST_OPTIONS_DEFAULT);
        System.out.println(createResp.isAcknowledged());
        System.out.println(createResp.index());
    }
    
    /**
     * 删除索引，也支持异步
     */
    @Test
    public void deleteIndex() throws IOException{
        DeleteIndexRequest request = new DeleteIndexRequest("twitter");
        AcknowledgedResponse deleteResp = restClient.indices().delete(request, REQUEST_OPTIONS_DEFAULT);
        System.out.println(deleteResp.isAcknowledged());
    }
    
    /**
     * 获取索引的mappings
     */
    @Test
    public void getIndexMapping() throws IOException{
        GetMappingsRequest request = new GetMappingsRequest();
        request.indices("twitter");
        GetMappingsResponse mappingResp = restClient.indices().getMapping(request, REQUEST_OPTIONS_DEFAULT);
        
        Map<String, MappingMetaData> allMappings = mappingResp.mappings();
        MappingMetaData mappingMetaData = allMappings.get("twitter");
        Map<String, Object> map = mappingMetaData.sourceAsMap();
        System.out.println(allMappings.size());
        System.out.println(JSON.toJSONString(map, true));
    }
    
    /**
     * 刷新索引
     */
    @Test
    public void refreshIndex() throws IOException{
        RefreshRequest request = new RefreshRequest("twitter");
        RefreshResponse refreshResp = restClient.indices().refresh(request, REQUEST_OPTIONS_DEFAULT);
        System.out.println(refreshResp.getTotalShards());
        System.out.println(refreshResp.getSuccessfulShards());
        System.out.println(refreshResp.getFailedShards());
    }
    
    /**
     * 关闭索引，打开索引
     */
    @Test
    public void closeAndOpenIndex() throws IOException{
        CloseIndexRequest closeReq = new CloseIndexRequest("twitter");
        AcknowledgedResponse close = restClient.indices().close(closeReq, REQUEST_OPTIONS_DEFAULT);
        System.out.println("close: " + close.isAcknowledged());
        
        OpenIndexRequest openReq = new OpenIndexRequest("twitter");
        OpenIndexResponse open = restClient.indices().open(openReq, REQUEST_OPTIONS_DEFAULT);
        System.out.println("open: " + open.isAcknowledged());
        System.out.println(open.isShardsAcknowledged());
    }
    
    /**
     * 获取别名，设置别名
     * @throws IOException 
     */
    @Test
    public void aliasIndex() throws IOException{
        // 根据别名获取索引
        GetAliasesRequest getAliasesRequest = new GetAliasesRequest("tw_alias");
        GetAliasesResponse getAliasResp = restClient.indices().getAlias(getAliasesRequest, REQUEST_OPTIONS_DEFAULT);
        Map<String, Set<AliasMetaData>> aliases = getAliasResp.getAliases();
        System.out.println(JSON.toJSONString(aliases));
        
        // 增加别名
        IndicesAliasesRequest aliasesRequest = new IndicesAliasesRequest();
        AliasActions addAction = new AliasActions(AliasActions.Type.ADD).index("twitter").alias("alias2");
        aliasesRequest.addAliasAction(addAction);
        AcknowledgedResponse addAliases = restClient.indices().updateAliases(aliasesRequest, REQUEST_OPTIONS_DEFAULT);
        System.out.println("add aliase: " + addAliases.isAcknowledged());
        
        // 删除别名
        AliasActions delAction = new AliasActions(AliasActions.Type.REMOVE).index("twitter").alias("alias2");
        aliasesRequest.addAliasAction(delAction);
        AcknowledgedResponse delAliases = restClient.indices().updateAliases(aliasesRequest, REQUEST_OPTIONS_DEFAULT);
        System.out.println("delete alias: " + delAliases.isAcknowledged());
    }
    
}
