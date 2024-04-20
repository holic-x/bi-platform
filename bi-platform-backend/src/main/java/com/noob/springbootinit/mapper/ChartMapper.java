package com.noob.springbootinit.mapper;

import com.noob.springbootinit.model.entity.Chart;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
* @author Huh-x
* @description 针对表【chart(图表信息表)】的数据库操作Mapper
* @createDate 2024-04-16 22:55:31
* @Entity com.noob.springbootinit.model.entity.Chart
*/
public interface ChartMapper extends BaseMapper<Chart> {

    List<Map<String,Object>> queryChartData(String querySql);

}




