package com.demon.lucene.book.chapter10;

import java.io.IOException;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentBuilder;

/**
 * 
 * @author xuliang
 * @since 2019年9月24日 下午1:59:01
 *
 */
public class ESUtils {

    private static RestHighLevelClient restClient = new RestHighLevelClient(RestClient.builder(new HttpHost("192.168.11.248", 9200, "http")));
    public static RequestOptions REQUEST_OPTIONS_DEFAULT = RequestOptions.DEFAULT;
    
    public static RestHighLevelClient getClient(){
        return restClient;
    }
    
    public static boolean createIndex(String indexName, int shards, int replicas, XContentBuilder builder) throws IOException{
        Settings settings = Settings.builder()
                        .put("index.number_of_shards", shards)
                        .put("index.number_of_replicas", replicas)
                        .build();
        CreateIndexRequest request = new CreateIndexRequest(indexName.toLowerCase());
        request.settings(settings);
        request.mapping(builder);
        CreateIndexResponse create = restClient.indices().create(request, REQUEST_OPTIONS_DEFAULT);
        boolean isAcknowledged = create.isAcknowledged();
        if(isAcknowledged){
            System.out.println("索引" + indexName + "创建成功");
        }else{
            System.out.println("索引" + indexName + "创建失败");
        }
        return isAcknowledged;
    }
    
}
