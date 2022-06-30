package com.atguigu.es;

import com.atguigu.es.bean.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.Set;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/6/30 8:09
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataESIndexTest {

    //注入 ElasticsearchRestTemplate
    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    //创建索引并增加映射配置
    @Test
    public void createIndex(){
        //创建索引，系统初始化会自动创建索引：自动关联项目中的实体类，如果没有当前实体类的索引就自动创建
        System.out.println("创建索引");
    }

    @Test
    public void getIndex() {
        IndexOperations indexOperations = elasticsearchRestTemplate.indexOps(Product.class);
        boolean exists = indexOperations.exists();
        Map<String, Object> mapping = indexOperations.getMapping();
        Set<Map.Entry<String, Object>> entries = mapping.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            Object value = entry.getValue();
        }
    }
}
