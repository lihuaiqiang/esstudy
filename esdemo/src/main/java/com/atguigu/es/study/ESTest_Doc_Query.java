package com.atguigu.es.study;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * 文档数据查询
 * <p>
 * 1、查询所有
 * 2、条件查询
 * 3、分页查询
 * 4、
 */
public class ESTest_Doc_Query {
    public static void main(String[] args) throws Exception {

        RestHighLevelClient esClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );


        // 1、查询索引下的所有数据
        SearchRequest request1 = new SearchRequest();
        request1.indices("user");
        SearchResponse response1 = esClient.search(request1, RequestOptions.DEFAULT);
        //System.out.println(response.getHits().getTotalHits());
        /*SearchHits hits1 = response.getHits();
        for (SearchHit hit : hits1) {
            String string = hit.getSourceAsString();
            System.out.println(string);
        }*/
        SearchHit[] hits1 = response1.getHits().getHits();
        for (SearchHit hit : hits1) {
            String source = hit.getSourceAsString();
            System.out.println(source);
        }


        // 2、条件查询  termQuery
        SearchRequest request2 = new SearchRequest();
        request2.indices("user");
        SearchSourceBuilder builder2 = new SearchSourceBuilder();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("age", 30);
        builder2.query(termQueryBuilder);
        request2.source(builder2);
        SearchResponse response2 = esClient.search(request2, RequestOptions.DEFAULT);
        SearchHit[] hits2 = response2.getHits().getHits();
        for (SearchHit hit : hits2) {
            String source = hit.getSourceAsString();
            //System.out.println(source);
        }


        //3、分页查询
        SearchRequest request3 = new SearchRequest();
        request3.indices("user");
        SearchSourceBuilder builder3 = new SearchSourceBuilder();
        MatchAllQueryBuilder matchAllQueryBuilder = QueryBuilders.matchAllQuery();
        builder3.query(matchAllQueryBuilder);
        //(当前页码-1)*每页显示数据条数
        builder3.from(5);
        builder3.size(5);
        request3.source(builder3);
        SearchResponse response3 = esClient.search(request3, RequestOptions.DEFAULT);
        SearchHit[] hits3 = response3.getHits().getHits();
        for (SearchHit hit : hits3) {
            String source = hit.getSourceAsString();
            //System.out.println("分页查询：" + source);
        }


        //4、查询排序
        SearchRequest request4 = new SearchRequest();
        request4.indices("user");
        SearchSourceBuilder builder4 = new SearchSourceBuilder();
        builder4.query(QueryBuilders.matchAllQuery());
        builder4.sort("age", SortOrder.DESC);
        request4.source(builder4);
        SearchResponse response4 = esClient.search(request4, RequestOptions.DEFAULT);
        SearchHit[] hits4 = response4.getHits().getHits();
        for (SearchHit hit : hits4) {
            String source = hit.getSourceAsString();
            System.out.println("查询排序：" + source);
        }

        //5、过滤字段  fetchSource
        SearchRequest request5 = new SearchRequest();
        request5.indices("user");
        SearchSourceBuilder builder5 = new SearchSourceBuilder();
        builder5.query(QueryBuilders.matchAllQuery());
        //过滤字段
        String[] include = {};
        String[] exclude = {"age"};
        builder5.fetchSource(include, exclude);
        request5.source(builder5);
        SearchResponse response5 = esClient.search(request5, RequestOptions.DEFAULT);
        SearchHit[] hits5 = response5.getHits().getHits();
        for (SearchHit hit : hits5) {
            String source = hit.getSourceAsString();
            //System.out.println(source);
        }


        //6、组合查询
        SearchRequest request6 = new SearchRequest();
        request6.indices("user");
        SearchSourceBuilder builder6 = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //boolQueryBuilder.must(QueryBuilders.matchQuery("name", "zhangsan"));//必须等于
        //boolQueryBuilder.must(QueryBuilders.matchQuery("age", "30"));//必须等于
        //boolQueryBuilder.mustNot(QueryBuilders.matchQuery("age", "30"));//必须不等于
        boolQueryBuilder.should(QueryBuilders.matchQuery("name", "wan")); //查询不出来
        boolQueryBuilder.should(QueryBuilders.matchQuery("name", "孙")); //能查询出来
        builder6.query(boolQueryBuilder);
        request6.source(builder6);
        SearchResponse response6 = esClient.search(request6, RequestOptions.DEFAULT);
        SearchHit[] hits6 = response6.getHits().getHits();
        for (SearchHit hit : hits6) {
            String source = hit.getSourceAsString();
            //System.out.println("组合查询：" + source);
        }

        //7、范围查询
        SearchRequest request7 = new SearchRequest();
        request7.indices("user");
        SearchSourceBuilder builder7 = new SearchSourceBuilder();
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("age");
        //rangeQueryBuilder.gt(30);//大于30
//        rangeQueryBuilder.gte(30);//大于等于30
        rangeQueryBuilder.lt(30);//小于30
        rangeQueryBuilder.lte(30);//小于等于30
        builder7.query(rangeQueryBuilder);
        request7.source(builder7);
        SearchResponse response7 = esClient.search(request7, RequestOptions.DEFAULT);
        SearchHit[] hits7 = response7.getHits().getHits();
        for (SearchHit hit : hits7) {
            String source = hit.getSourceAsString();
            //System.out.println("范围查询：" + source);
        }


        //8、模糊查询
        SearchRequest request8 = new SearchRequest();
        request8.indices("user");
        SearchSourceBuilder builder8 = new SearchSourceBuilder();
        //fuzziness(Fuzziness.TWO)：相差几个字符可以查询出来
        //FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("name", "zhang").fuzziness(Fuzziness.TWO);
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("name", "孙");
        builder8.query(fuzzyQueryBuilder);
        request8.source(builder8);
        SearchResponse response8 = esClient.search(request8, RequestOptions.DEFAULT);
        SearchHit[] hits8 = response8.getHits().getHits();
        for (SearchHit hit : hits8) {
            String source = hit.getSourceAsString();
            System.out.println("模糊查询：" + source);
        }


        //9、高亮查询
        SearchRequest request9 = new SearchRequest();
        request9.indices("user");
        SearchSourceBuilder builder9 = new SearchSourceBuilder();
        FuzzyQueryBuilder fuzzyQueryBuilder9 = QueryBuilders.fuzzyQuery("name", "孙");//FuzzyQueryBuilder没有实现高亮的效果
        TermQueryBuilder termQueryBuilder1 = QueryBuilders.termQuery("name", "zhangsan");//TermQueryBuilder实现了高亮的效果

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");
        highlightBuilder.field("name");

        builder9.highlighter(highlightBuilder);
        builder9.query(termQueryBuilder1);

        request9.source(builder9);
        SearchResponse response9 = esClient.search(request9, RequestOptions.DEFAULT);

        SearchHit[] hits9 = response9.getHits().getHits();

        for (SearchHit hit : hits9) {
            String source = hit.getSourceAsString();
            //System.out.println("模糊查询：" + source);
        }


        //10、聚合查询
        SearchRequest request10 = new SearchRequest();
        request10.indices("user");
        SearchSourceBuilder builder10 = new SearchSourceBuilder();

        AggregationBuilder maxAggregationBuilder = AggregationBuilders.max("maxage").field("age");
        AggregationBuilder minAggregationBuilder = AggregationBuilders.min("minage").field("age");
        AggregationBuilder avgAggregationBuilder = AggregationBuilders.avg("avg_age").field("age");
        builder10.aggregation(maxAggregationBuilder);
        builder10.aggregation(minAggregationBuilder);
        builder10.aggregation(avgAggregationBuilder);

        request10.source(builder10);
        SearchResponse response10 = esClient.search(request10, RequestOptions.DEFAULT);

        SearchHit[] hits10 = response10.getHits().getHits();
        System.out.println("聚合查询：" + response10.getHits().getTotalHits());
        for (SearchHit hit : hits10) {
            String source = hit.getSourceAsString();
            //System.out.println("聚合查询：" + source);
        }


        //11、分组查询
        SearchRequest request11 = new SearchRequest();
        request11.indices("user");
        SearchSourceBuilder builder11 = new SearchSourceBuilder();

        AggregationBuilder termsAggregationBuilder = AggregationBuilders.terms("age_group").field("age");
        builder11.aggregation(termsAggregationBuilder);

        //顺便排了个序
        builder11.sort("age", SortOrder.ASC);

        request11.source(builder11);
        SearchResponse response11 = esClient.search(request11, RequestOptions.DEFAULT);

        SearchHit[] hits11 = response11.getHits().getHits();
        System.out.println("分组查询：" + response11.getHits().getTotalHits());
        for (SearchHit hit : hits11) {
            String source = hit.getSourceAsString();
            System.out.println("分组查询：" + source);
        }


        esClient.close();
    }
}
