package com.atguigu.es.study;

import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

public class ESTest_Doc_Insert_Batch2 {
    public static void main(String[] args) throws Exception {

        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        // 文档数据批量插入
        BulkRequest request = new BulkRequest();

        request.add(new IndexRequest().index("user").id("1003").source(XContentType.JSON, "name", "wangwu", "sex", "女", "age", "19"));
        request.add(new IndexRequest().index("user").id("1005").source(XContentType.JSON, "name", "孙准浩", "sex", "男", "age", "28"));
        request.add(new IndexRequest().index("user").id("1006").source(XContentType.JSON, "name", "费莱尼", "sex", "男", "age", "33"));

        BulkResponse response = esClient.bulk(request, RequestOptions.DEFAULT);

        // 响应状态
        System.out.println("索引操作 ：" + response.getIngestTook());

        esClient.close();
    }
}
