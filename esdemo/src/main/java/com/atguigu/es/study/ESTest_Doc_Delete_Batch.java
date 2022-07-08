package com.atguigu.es.study;

import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * 按照索引批量删除 ==> 测试通过
 */
public class ESTest_Doc_Delete_Batch {
    public static void main(String[] args) throws Exception {

        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 文档数据批量删除
        BulkRequest request = new BulkRequest();
        request.add(new DeleteRequest().index("user").id("1001"));
        request.add(new DeleteRequest().index("user").id("1002"));
        BulkResponse bulkDeleteResponse = esClient.bulk(request, RequestOptions.DEFAULT);
        BulkItemResponse[] items = bulkDeleteResponse.getItems();
        for (BulkItemResponse item : items) {
            DocWriteResponse response = item.getResponse();
            System.out.println(response);
        }
        // 响应状态
        System.out.println("索引操作 ：" + bulkDeleteResponse.toString());

        esClient.close();
    }
}
