package com.atguigu.es.study;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * 文档数据查询
 * <p>
 * 1、查询所有
 * 2、条件查询
 * 3、分页查询
 * 4、
 */
public class ESTest_Doc_Query_All {
    public static void main(String[] args) throws Exception {

        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );


        // 1、查询索引下的所有数据
        SearchRequest request1 = new SearchRequest();
        request1.indices("user");
        SearchSourceBuilder builder = new SearchSourceBuilder();
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        builder.query(matchAllQueryBuilder);
        request1.source(builder);
        SearchResponse response1 = esClient.search(request1, RequestOptions.DEFAULT);
        System.out.println(response1.getHits().getTotalHits());
        SearchHit[] hits1 = response1.getHits().getHits();
        for (SearchHit hit : hits1) {
            String source = hit.getSourceAsString();
            System.out.println("查询全部：" + source);
        }

        esClient.close();
    }
}
