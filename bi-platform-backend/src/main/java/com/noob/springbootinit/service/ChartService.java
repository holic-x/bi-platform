package com.noob.springbootinit.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.springbootinit.model.dto.chart.ChartQueryRequest;
import com.noob.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.extension.service.IService;
import com.noob.springbootinit.model.entity.Chart;
import com.noob.springbootinit.model.vo.ChartVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author hahabibu
* @description 针对表【chart(图表信息表)】的数据库操作Service
* @createDate 2024-04-16 22:55:31
*/
public interface ChartService extends IService<Chart> {

    /**
     * 校验
     *
     * @param chart
     * @param add
     */
    void validChart(Chart chart, boolean add);

    /**
     * 获取查询条件
     *
     * @param chartQueryRequest
     * @return
     */
    QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest);


    /**
     * 获取图表封装
     *
     * @param chart
     * @param request
     * @return
     */
    ChartVO getChartVO(Chart chart, HttpServletRequest request);

    /**
     * 分页获取图表封装
     *
     * @param chartPage
     * @param request
     * @return
     */
    Page<ChartVO> getChartVOPage(Page<Chart> chartPage, HttpServletRequest request);
    

}
