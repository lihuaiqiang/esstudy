package com.atguigu.es.study;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

public class ESTest_Index_Delete {
    public static void main(String[] args) throws Exception {

        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 删除索引
        DeleteIndexRequest request = new DeleteIndexRequest("person");
        AcknowledgedResponse delete = esClient.indices().delete(request, RequestOptions.DEFAULT);

        // 响应状态
        boolean acknowledged = delete.isAcknowledged();
        System.out.println("索引操作 ：" + acknowledged);

        esClient.close();
    }
}
