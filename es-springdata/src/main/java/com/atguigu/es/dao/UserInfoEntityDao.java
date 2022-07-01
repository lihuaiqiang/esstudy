package com.atguigu.es.dao;

import com.atguigu.es.bean.UserInfoEntity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author lihuaiqiang
 * @description
 * @date 2022/7/1 15:59
 */
@Repository
public interface UserInfoEntityDao extends ElasticsearchRepository<UserInfoEntity, String> {

}
