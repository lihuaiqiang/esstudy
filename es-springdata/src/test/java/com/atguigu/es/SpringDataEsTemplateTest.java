package com.atguigu.es;

import com.atguigu.es.bean.Product;
import com.atguigu.es.bean.UserInfoEntity;
import com.atguigu.es.util.PageHelper;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FuzzyQueryBuilder;
import org.elasticsearch.index.query.MatchPhraseQueryBuilder;
import org.elasticsearch.index.query.PrefixQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.NestedSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilterBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.data.elasticsearch.core.query.UpdateQuery;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/6/30 10:56
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataEsTemplateTest {

    @Autowired
    private ElasticsearchRestTemplate esTemplate;

    /**
     * ????????????
     */
    @Test
    public void saveTest() {
        Product product = new Product();
        product.setId(1002L);
        product.setTitle("?????????-???");
        product.setCategory("?????????");
        product.setPrice(150.88D);
        product.setImages("http://baidu.com/1.png");
        //?????????????????????????????????
        esTemplate.save(product);

        Product product2 = new Product();
        product2.setId(1001L);
        product2.setTitle("??????2??????");
        product2.setCategory("??????");
        product2.setPrice(1d);
        product2.setImages("http://www.atguigu/xm.jpg");
        esTemplate.save(product2);
    }

    /**
     * ?????????????????????
     */
    @Test
    public void getDocumentAll() {
        NativeSearchQuery query = new NativeSearchQueryBuilder().build();
        SearchHits<Product> search = esTemplate.search(query, Product.class);
        //List<Product> productList1 = esTemplate.queryForList(query, Product.class, IndexCoordinates.of("product"));
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> productList = searchHits.stream().map(i -> i.getContent()).collect(Collectors.toList());
        for (Product userInfoEntity : productList) {
            System.out.println("?????????????????????" + userInfoEntity);
        }
        System.out.println(search.getTotalHits());
    }

    /**
     * ??????ID??????
     */
    @Test
    public void getDocumentById() {
        if (esTemplate.exists(String.valueOf(1001L), Product.class)) {
            Product product = esTemplate.get(String.valueOf(1001L), Product.class);
            System.out.println("product???" + product);
        }
        if (esTemplate.indexOps(Product.class).exists()) {
            IndexOperations indexOperations = esTemplate.indexOps(Product.class);
            System.out.println(indexOperations);
        }
    }

    /**
     * ??????
     */
    @Test
    public void update() {
        Product product = new Product();
        product.setId(9L);
        //product.setTitle("?????? 2 ??????");
        //product.setCategory("??????");
        product.setPrice(10d);
        //product.setImages("http://www.atguigu/xm.jpg");
        Document document = Document.create();
        document.append("userOrder", 20);
        UpdateQuery updateQuery = UpdateQuery.builder(String.valueOf(27L)).withDocument(document).build();
        esTemplate.update(updateQuery, IndexCoordinates.of("product"));
    }

    /**
     * ??????
     */
    @Test
    public void delete() {
        Product product = new Product();
        product.setId(1001L);
        String delete = esTemplate.delete(product);
        boolean exists = esTemplate.exists(String.valueOf(1L), Product.class);
        System.out.println(delete);
    }

    /**
     * ????????????
     */
    @Test
    public void saveAll() {
        List<Product> productList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Product product = new Product();
            product.setId(Long.valueOf(i + 30));
            product.setTitle("?????????" + i);
            product.setCategory("???????????????");
            product.setPrice(10.0 + i);
            product.setImages("http://www.atguigu/xm.jpg");
            product.setCode((i + 1) + "0001000" + (i + 1));
            product.setPositionOrder(new BigDecimal("0." + product.getCode()));
            product.setUserOrder(new BigDecimal(product.getId() - 1));
            productList.add(product);
        }
        esTemplate.save(productList);
    }

    public String dealOrder(String order){
        if(order.length() == 1)
            order ="00" + order;
        if(order.length() == 2)
            order ="0" + order;
        return order;
    }

    /**
     * ????????????
     */
    @Test
    public void saveAll2() {
        List<UserInfoEntity> userInfoList = new ArrayList<>();
        //79500 79900 80000  85000 87000 87400 87450  ??????
        //87480 87500  ?????????
        for (int i = 1; i <= 50500; i++) {
            UserInfoEntity userInfoEntity = new UserInfoEntity();
            userInfoEntity.setId(String.valueOf(i));
            userInfoEntity.setMainPosition(false);
            userInfoEntity.setName("??????" + i + 10);
            if (i % 2 == 0) {
                userInfoEntity.setGender("???");
            } else {
                userInfoEntity.setGender("???");
            }
            userInfoEntity.setOrgCode("100" + i + 10);
            userInfoList.add(userInfoEntity);
        }
//        Long statTime = System.currentTimeMillis();
//        esTemplate.save(userInfoList);
//        System.out.println("????????????UserTask????????????,??????:{}" + (System.currentTimeMillis() - statTime) / 1000);
        List<List<UserInfoEntity>> splistList = splistList(userInfoList, 10000);
        for (List<UserInfoEntity> userInfoEntityList : splistList) {
            System.out.println(userInfoEntityList.size());
        }
    }

    public static <T> List<List<T>> splistList(List<T> list, int subNum) {
        List<List<T>> tNewList = new ArrayList<List<T>>();
        int priIndex = 0;
        int lastPriIndex = 0;
        int insertTimes = list.size() / subNum;
        List<T> subList = new ArrayList<>();
        for (int i = 0; i <= insertTimes; i++) {
            priIndex = subNum * i;
            lastPriIndex = priIndex + subNum;
            if (i == insertTimes) {
                subList = list.subList(priIndex, list.size());
            } else {
                subList = list.subList(priIndex, lastPriIndex);
            }
            if (subList.size() > 0) {
                tNewList.add(subList);
            }
        }
        return tNewList;
    }

    /**
     * ???????????????????????????
     */
    @Test
    public void findBySort() {
        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery("title", "??????");
        FieldSortBuilder sortBuilder = new FieldSortBuilder("positionOrder").order(SortOrder.DESC);
        FieldSortBuilder sortBuilder2 = new FieldSortBuilder("userOrder").order(SortOrder.ASC);
        FieldSortBuilder sortBuilder3 = new FieldSortBuilder("code.keyword").order(SortOrder.DESC);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(prefixQueryBuilder)
                //.withPageable(pageRequest)
                //.withSourceFilter(sourceFilter)
                .withSort(sortBuilder2)//PageRequest??????????????????????????????????????????????????????
                .withSort(sortBuilder)//PageRequest??????????????????????????????????????????????????????
                .withSort(sortBuilder3)//PageRequest??????????????????????????????????????????????????????
                .build();
        //????????????
        AggregatedPage<Product> aggregatedPage = esTemplate.queryForPage(query, Product.class, IndexCoordinates.of("product"));
        for (Product product : aggregatedPage) {
            System.out.println(product);
        }
    }

    /**
     * ?????????????????????????????????
     */
    @Test
    public void multiFieldsSort() {
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "positionOrder.keyword"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "order.keyword"));
        orders.add(new Sort.Order(Sort.Direction.ASC, "id.keyword"));
        Sort sort = Sort.by(orders);
        int currentPage = 0;//???????????????????????? 0 ?????????1 ???????????????
        int pageSize = 10;//?????????????????????
        //??????????????????
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("name", "??????");//???????????????????????????
        boolQueryBuilder.must(matchPhraseQueryBuilder);
        boolQueryBuilder.must(QueryBuilders.termQuery("searchCode", "0000100001"));
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withPageable(pageRequest)
                .build();
        nativeSearchQuery.setTrackTotalHits(true);
        SearchHits<UserInfoEntity> search = esTemplate.search(nativeSearchQuery, UserInfoEntity.class);
        List<SearchHit<UserInfoEntity>> searchHits = search.getSearchHits();
        List<UserInfoEntity> collect = searchHits.stream().map(e -> e.getContent()).collect(Collectors.toList());
        List<UserInfoEntity> productList = search.get().map(i -> i.getContent()).collect(Collectors.toList());
        for (UserInfoEntity product : collect) {
            System.out.println(product);
        }
    }

    /**
     * ?????????????????????
     */
    @Test
    public void findByPageable() {
        //???????????????
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.ASC, "price"));
        orders.add(new Sort.Order(Sort.Direction.DESC, "id"));
//        orders.add(new Sort.Order(Sort.Direction.ASC, "id.keyword"));
        Sort sort = Sort.by(orders);

        //????????????(????????????????????????????????????????????? id)
//        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        int currentPage = 0;//???????????????????????? 0 ?????????1 ???????????????
        int pageSize = 100;//?????????????????????
        //??????????????????
        PageRequest pageRequest = PageRequest.of(currentPage, pageSize, sort);

        //?????????????????????????????????????????????????????????????????? "title", "category"
        String[] includes = new String[]{};
        String[] excludes = new String[]{};
        SourceFilter sourceFilter = new FetchSourceFilterBuilder().withIncludes(includes).withExcludes(excludes).build();

        FieldSortBuilder sortBuilder = new FieldSortBuilder("price").order(SortOrder.DESC);
        FieldSortBuilder sortBuilder2 = new FieldSortBuilder("id").order(SortOrder.ASC);
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                //.withPageable(pageRequest)
                //.withSourceFilter(sourceFilter)
                .withSort(sortBuilder)//PageRequest??????????????????????????????????????????????????????
                .withSort(sortBuilder2)//PageRequest??????????????????????????????????????????????????????
                .build();
        //????????????
        AggregatedPage<Product> aggregatedPage = esTemplate.queryForPage(query, Product.class, IndexCoordinates.of("product"));
        for (Product product : aggregatedPage) {
            System.out.println(product);
        }
        int number = aggregatedPage.getNumber();
        int size = aggregatedPage.getSize();
        int numberOfElements = aggregatedPage.getNumberOfElements();
        int totalPages = aggregatedPage.getTotalPages();
        long totalElements = aggregatedPage.getTotalElements();
        List<Product> products = aggregatedPage.toList();
        List<Product> content = aggregatedPage.getContent();
        /*Page<Product> productPage = productDao.findAll(pageRequest);
        for (Product product : productPage.getContent()) {
            System.out.println(product);
        }*/
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????????????????
     */
    @Test
    public void fuzzyQuery() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("title", "??????");//???????????????????????????
        boolQueryBuilder.must(matchPhraseQueryBuilder);
        FieldSortBuilder sortBuilder2 = new FieldSortBuilder("id").order(SortOrder.DESC);
        FieldSortBuilder sortBuilder = new FieldSortBuilder("price").order(SortOrder.DESC);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withSort(sortBuilder)
                .withSort(sortBuilder2)
                .build();
        nativeSearchQuery.setTrackTotalHits(true);
        SearchHits<Product> search = esTemplate.search(nativeSearchQuery, Product.class);
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> collect = searchHits.stream().map(e -> e.getContent()).collect(Collectors.toList());
        List<Product> productList = search.get().map(i -> i.getContent()).collect(Collectors.toList());
        for (Product product : collect) {
            System.out.println(product);
        }
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    @Test
    public void fuzzyQuery2() {
        /*SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("title","???"));*/
        FuzzyQueryBuilder fuzzyQueryBuilder = QueryBuilders.fuzzyQuery("title", "??????");

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(fuzzyQueryBuilder).build();

        SearchHits<Product> search = esTemplate.search(nativeSearchQuery, Product.class);
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> collect = searchHits.stream().map(e -> e.getContent()).collect(Collectors.toList());
        for (Product product : collect) {
            System.out.println("???????????????" + product);
        }
    }

    /**
     * ???????????????????????????
     */
    @Test
    public void prefixQuery() {
        /*SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("title","???"));*/
        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery("code", "10001");

        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(prefixQueryBuilder).build();

        SearchHits<Product> search = esTemplate.search(nativeSearchQuery, Product.class);
        boolean b = search.hasSearchHits();
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> collect = searchHits.stream().map(e -> e.getContent()).collect(Collectors.toList());
        for (Product product : collect) {
            System.out.println("???????????????" + product);
        }
    }

    /**
     * ?????????????????????
     */
    @Test
    public void prefixQuery2() {
        /*SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("title","???"));*/
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        PrefixQueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery("orgCode", "0000100001");
        PrefixQueryBuilder prefixQueryBuilder2 = QueryBuilders.prefixQuery("orgCode", "000010000100179");
        boolQueryBuilder.should(prefixQueryBuilder);
        boolQueryBuilder.should(prefixQueryBuilder2);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(prefixQueryBuilder)
                .build();
        Long statTime = System.currentTimeMillis();
        SearchHits<UserInfoEntity> search = esTemplate.search(nativeSearchQuery, UserInfoEntity.class);
        System.out.println(("????????????UserTask????????????,??????:{}" + (System.currentTimeMillis() - statTime) ));
        boolean b = search.hasSearchHits();
        List<SearchHit<UserInfoEntity>> searchHits = search.getSearchHits();
        List<UserInfoEntity> collect = searchHits.stream().map(e -> e.getContent()).collect(Collectors.toList());
        for (UserInfoEntity product : collect) {
            System.out.println("???????????????" + product);
        }
    }

    /**
     * ??????????????????????????????
     */
    @Test
    public void deleteByPrefixQuery() {
        System.out.println("=============??????????????????==============");
        NativeSearchQuery query = new NativeSearchQueryBuilder().build();
        SearchHits<Product> search = esTemplate.search(query, Product.class);
        List<SearchHit<Product>> searchHits = search.getSearchHits();
        List<Product> productList = searchHits.stream().map(i -> i.getContent()).collect(Collectors.toList());
        for (Product product : productList) {
            System.out.println("?????????????????????" + product);
        }
        System.out.println("?????????????????????" + search.getTotalHits());

        System.out.println("=============????????????==============");

        PrefixQueryBuilder prefixQueryBuilder2 = QueryBuilders.prefixQuery("code", "20001");
        NativeSearchQuery nativeSearchQuery2 = new NativeSearchQueryBuilder().withQuery(prefixQueryBuilder2).build();
        esTemplate.delete(nativeSearchQuery2, Product.class, esTemplate.getIndexCoordinatesFor(Product.class));

        System.out.println("=============??????????????????==============");

        NativeSearchQuery query2 = new NativeSearchQueryBuilder().build();
        SearchHits<Product> search2 = esTemplate.search(query2, Product.class);
        List<SearchHit<Product>> searchHits2 = search2.getSearchHits();
        List<Product> productList2 = searchHits2.stream().map(i -> i.getContent()).collect(Collectors.toList());
        for (Product product : productList2) {
            System.out.println("?????????????????????" + product);
        }
        System.out.println("?????????????????????" + search2.getTotalHits());
    }

    /**
     * ???????????????????????????????????????
     */
    @Test
    public void deleteBySearchQuery() {
        System.out.println("=============??????????????????==============");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("searchCode", "000010006600003");
        boolQueryBuilder.must(termQueryBuilder);
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        SearchHits<UserInfoEntity> search = esTemplate.search(nativeSearchQuery, UserInfoEntity.class);
        List<SearchHit<UserInfoEntity>> searchHits = search.getSearchHits();
        List<UserInfoEntity> userInfoEntityList = searchHits.stream().map(i -> i.getContent()).collect(Collectors.toList());
        for (UserInfoEntity userInfoEntity : userInfoEntityList) {
            System.out.println("?????????????????????" + userInfoEntity);
        }
        System.out.println("?????????????????????" + search.getTotalHits());

        System.out.println("UserInfoEntity?????????????????????" + esTemplate.indexOps(UserInfoEntity.class).exists());
        System.out.println("=============????????????==============");

        BoolQueryBuilder boolQueryBuilder2 = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder2 = QueryBuilders.termQuery("searchCode", "000010006600003");
        boolQueryBuilder2.must(termQueryBuilder2);
        NativeSearchQuery nativeSearchQuery2 = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder2).build();
        esTemplate.delete(nativeSearchQuery2, UserInfoEntity.class, esTemplate.getIndexCoordinatesFor(UserInfoEntity.class));

        System.out.println("=============??????????????????==============");
        BoolQueryBuilder boolQueryBuilder3 = QueryBuilders.boolQuery();
        TermQueryBuilder termQueryBuilder3 = QueryBuilders.termQuery("searchCode", "000010006600003");
        boolQueryBuilder3.must(termQueryBuilder3);
        NativeSearchQuery nativeSearchQuery3 = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder3).build();
        SearchHits<UserInfoEntity> search3 = esTemplate.search(nativeSearchQuery3, UserInfoEntity.class);
        List<SearchHit<UserInfoEntity>> searchHits3 = search3.getSearchHits();
        List<UserInfoEntity> userInfoEntityList3 = searchHits3.stream().map(i -> i.getContent()).collect(Collectors.toList());
        for (UserInfoEntity userInfoEntity : userInfoEntityList3) {
            System.out.println("?????????????????????" + userInfoEntity);
        }
        System.out.println("?????????????????????" + search3.getTotalHits());
    }

    /**
     * ?????????????????????????????????????????????????????????
     */
    @Test
    public void deleteEsBySearchQuery2() {
        System.out.println("UserInfoEntity?????????????????????" + esTemplate.indexOps(UserInfoEntity.class).exists());
        if (esTemplate.indexOps(UserInfoEntity.class).exists()) {
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("searchCode", "0000100066010007700499001");
            boolQueryBuilder.must(termQueryBuilder);
            NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
            SearchHits<UserInfoEntity> search = esTemplate.search(nativeSearchQuery, UserInfoEntity.class);
            System.out.println("???????????????" + search.getTotalHits());
            if (search.hasSearchHits()) {
                List<SearchHit<UserInfoEntity>> searchHits3 = search.getSearchHits();
                List<UserInfoEntity> userInfoEntityList3 = searchHits3.stream().map(i -> i.getContent()).collect(Collectors.toList());
                for (UserInfoEntity userInfoEntity : userInfoEntityList3) {
                    System.out.println("?????????????????????" + userInfoEntity);
                }
                esTemplate.delete(nativeSearchQuery, UserInfoEntity.class, esTemplate.getIndexCoordinatesFor(UserInfoEntity.class));
            }
        }
    }

    /**
     * ?????????poc??????????????????????????????????????????????????????
     */
    @Test
    public void fuzzyQuery3() {
        int currentPage = 0;//???????????????????????? 0 ?????????1 ???????????????
        int pageSize = 5;//?????????????????????
        String userName = "???";
        String orgCode = "100100100";
        PageRequest page = PageRequest.of(0, 4);
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        if (StringUtils.isNotBlank(userName)) {
            MatchPhraseQueryBuilder matchPhraseQueryBuilder = QueryBuilders.matchPhraseQuery("name", userName);
//            MatchQueryBuilder matchQueryBuilder = (MatchQueryBuilder) matchPhraseQueryBuilder;
            boolQueryBuilder.must(matchPhraseQueryBuilder);
        }
        FieldSortBuilder sortBuilder = new FieldSortBuilder("id.keyword").order(SortOrder.DESC);
        //boolQueryBuilder.must(QueryBuilders.termQuery("searchCode", orgCode));
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(boolQueryBuilder)
                .withSort(sortBuilder)
                .withPageable(page)
                .build();
        searchQuery.setTrackTotalHits(true);
        SearchHits<UserInfoEntity> search = esTemplate.search(searchQuery, UserInfoEntity.class);
        List<SearchHit<UserInfoEntity>> searchHits = search.getSearchHits();
        Long totalHits = search.getTotalHits();
        List<UserInfoEntity> list = searchHits.stream().map(i -> i.getContent()).collect(Collectors.toList());
        for (UserInfoEntity userInfoEntity : list) {
            System.out.println("userInfoEntity???" + userInfoEntity);
        }
        PageHelper<UserInfoEntity> pageHelper = new PageHelper<UserInfoEntity>(list, totalHits.intValue(), pageSize, currentPage + 1);
    }

    /**
     * ???????????????
     */
    @Test
    public void multiQueryParam() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("title", "???"));
        boolQueryBuilder.should(QueryBuilders.rangeQuery("price").gt(200));
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        SearchHits<Product> search = esTemplate.search(nativeSearchQuery, Product.class);
        List<Product> productList = search.getSearchHits().stream().map(e -> e.getContent()).collect(Collectors.toList());
        for (Product product : productList) {
            System.out.println(product);
        }
    }

    /**
     * ???????????????
     */
    @Test
    public void multiQueryParam2() {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        List<String> orgCodeList = Arrays.asList("0000100001040007702399001", "0000100001040007702388002");
        boolQueryBuilder.must(QueryBuilders.termsQuery("searchCode", orgCodeList));
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder).build();
        SearchHits<UserInfoEntity> search = esTemplate.search(nativeSearchQuery, UserInfoEntity.class);
        List<UserInfoEntity> productList = search.getSearchHits().stream().map(e -> e.getContent()).collect(Collectors.toList());
        for (UserInfoEntity product : productList) {
            System.out.println(product);
        }
    }

    /**
     * ???????????????
     */
    @Test
    public void queryParam() {
        //BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //boolQueryBuilder.must(QueryBuilders.matchQuery("title", "???"));
        //boolQueryBuilder.should(QueryBuilders.rangeQuery("price").gt(200));
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", "??????7");
        NativeSearchQuery nativeSearchQuery = new NativeSearchQueryBuilder()
                .withQuery(termQueryBuilder)
                .build();
        SearchHits<Product> search = esTemplate.search(nativeSearchQuery, Product.class);
        List<Product> productList = search.getSearchHits().stream().map(e -> e.getContent()).collect(Collectors.toList());
        for (Product product : productList) {
            System.out.println(product);
        }
    }
}
