package com.atguigu.es.dao;

import com.atguigu.es.bean.Product;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/6/29 19:46
 */
@Repository
public interface ProductDao extends ElasticsearchRepository<Product,Long> {

}
