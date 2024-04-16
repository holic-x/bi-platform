package com.noob.springbootinit.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.noob.springbootinit.model.entity.Post;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 图表视图
 *
 */
@Data
public class ChartVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表名称
     */
    private String name;

    /**
     * 图表数据
     */
    private String chartData;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 生成的图表数据
     */
    private String genChart;

    /**
     * 生成的分析结论
     */
    private String genResult;

    /**
     * wait,running,succeed,failed
     */
    private String status;

    /**
     * 执行信息
     */
    private String execMessage;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 包装类转对象
     *
     * @param postVO
     * @return
     */
//    public static Post voToObj(ChartVO postVO) {
//        if (postVO == null) {
//            return null;
//        }
//        Post post = new Post();
//        BeanUtils.copyProperties(postVO, post);
//        List<String> tagList = postVO.getTagList();
//        post.setTags(JSONUtil.toJsonStr(tagList));
//        return post;
//    }

    /**
     * 对象转包装类
     *
     * @param post
     * @return
     */
//    public static ChartVO objToVo(Post post) {
//        if (post == null) {
//            return null;
//        }
//        ChartVO postVO = new ChartVO();
//        BeanUtils.copyProperties(post, postVO);
//        postVO.setTagList(JSONUtil.toList(post.getTags(), String.class));
//        return postVO;
//    }
}
