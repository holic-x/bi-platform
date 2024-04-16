package com.noob.springbootinit.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.noob.springbootinit.model.entity.Chart;
import com.noob.springbootinit.service.ChartService;
import com.noob.springbootinit.mapper.ChartMapper;
import org.springframework.stereotype.Service;

/**
* @author hahabibu
* @description 针对表【chart(图表信息表)】的数据库操作Service实现
* @createDate 2024-04-16 22:55:31
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService{

}




