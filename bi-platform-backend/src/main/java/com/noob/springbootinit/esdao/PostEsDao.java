package com.noob.springbootinit.esdao;

import com.noob.springbootinit.model.dto.post.PostEsDTO;

import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 帖子 ES 操作
 */
public interface PostEsDao extends ElasticsearchRepository<PostEsDTO, Long> {

    // 根据用户id查找帖子信息（此处借助ElasticsearchRepository按照指定规则定义方法名，其会自动生成符合既定规则的方法实现，无需额外自定义实现）
    List<PostEsDTO> findByUserId(Long userId);
}