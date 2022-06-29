package com.atguigu.es.study;

import org.apache.http.HttpHost;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;

/**
 * 按照索引删除 ==> 测试通过
 */
public class ESTest_Doc_Delete {
    public static void main(String[] args) throws Exception {

        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 文档数据删除
        DeleteRequest request = new DeleteRequest();
        request.index("user").id("1001");
        DeleteResponse deleteResponse = esClient.delete(request, RequestOptions.DEFAULT);

        // 响应状态
        System.out.println("索引操作 ：" + deleteResponse.getResult());

        esClient.close();
    }
}
