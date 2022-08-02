package com.atguigu.es;

import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/8/1 14:51
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MyEsTest {

    @Qualifier("esRestClient")
    @Autowired
    private RestHighLevelClient client;

    @Test
    public void testSearchData() throws IOException {
        //1、创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        //指定索引
        searchRequest.indices("bank");
        //指定检索条件：DSL
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        //1-1构造检索条件
        /*searchSourceBuilder.query();
        searchSourceBuilder.from(1);
        searchSourceBuilder.size(10);*/
        searchSourceBuilder.query(QueryBuilders.matchQuery("address", "mill"));

        //1-2构造聚合条件：按照年龄聚合，年龄分布
        TermsAggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("aggAge").field("age");
        searchSourceBuilder.aggregation(termsAggregationBuilder);
        //1-3 平均薪资
        AvgAggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("balance").field("balance");
        searchSourceBuilder.aggregation(avgAggregationBuilder);

        //把构建的查询条件传到检索请求中
        searchRequest.source(searchSourceBuilder);
        System.out.println("检索条件：" + searchSourceBuilder);


        //2、执行检索
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        //3、分析数据
        //获取查询到的数据
        TotalHits totalHits = searchResponse.getHits().getTotalHits();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            String sourceAsString = hit.getSourceAsString();
            System.out.println("查询结果：" + hit.getSourceAsString());
        }
        //获取分析数据，如聚合、平均值等
        Aggregations aggregations = searchResponse.getAggregations();
        Terms terms = aggregations.get("aggAge");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println("年龄：" + bucket.getKeyAsString());
            System.out.println("分布数量：" + bucket.getDocCount());
        }
        Avg avgBalance  = aggregations.get("balance");
        System.out.println("name：" + avgBalance.getName());
        System.out.println("value：" + avgBalance.getValue());
        /*for (Aggregation aggregation : aggregations) {
            String name = aggregation.getName();
            System.out.println("聚合查询结果：" + name);
            System.out.println("聚合查询结果：" + aggregation.toString());
        }*/
    }

    @Test
    public void testInsertData() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder();
        builder.startObject();
        {
            builder.field("user", "kimchy");
            builder.timeField("postDate", new Date());
            builder.field("message", "trying out Elasticsearch");
        }
        builder.endObject();
        IndexRequest indexRequest = new IndexRequest("posts").id("1").source(builder);

        //es执行保存数据的操作
        IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println("testInsertData结果：" + response.getResult());
    }

    @Test
    public void test1() {
        System.out.println("单元测试....");
        System.out.println("client：" + client);
    }
}
