package com.noob.springbootinit.model.dto.chart;

import com.noob.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ChartQueryRequest extends PageRequest implements Serializable {

    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 分析目标
     */
    private String goal;

    /**
     * 图表类型
     */
    private String chartType;

    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}