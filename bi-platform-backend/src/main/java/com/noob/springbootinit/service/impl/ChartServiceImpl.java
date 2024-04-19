package com.noob.springbootinit.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noob.springbootinit.common.ErrorCode;
import com.noob.springbootinit.exception.BusinessException;
import com.noob.springbootinit.exception.ThrowUtils;
import com.noob.springbootinit.model.dto.chart.ChartQueryRequest;
import com.noob.springbootinit.model.entity.Chart;
import com.noob.springbootinit.model.vo.ChartVO;
import com.noob.springbootinit.service.ChartService;
import com.noob.springbootinit.mapper.ChartMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
* @author Huh-x
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-04-16 22:55:31
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

    @Override
    public void validChart(Chart chart, boolean add) {

        // todo 图表数据校验

    }

    @Override
    public QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        return null;
    }

    @Override
    public ChartVO getChartVO(Chart chart, HttpServletRequest request) {
        return null;
    }

    @Override
    public Page<ChartVO> getChartVOPage(Page<Chart> chartPage, HttpServletRequest request) {
        return null;
    }


}




