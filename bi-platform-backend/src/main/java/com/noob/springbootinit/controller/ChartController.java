package com.noob.springbootinit.controller;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.noob.springbootinit.annotation.AuthCheck;
import com.noob.springbootinit.common.BaseResponse;
import com.noob.springbootinit.common.DeleteRequest;
import com.noob.springbootinit.common.ErrorCode;
import com.noob.springbootinit.common.ResultUtils;
import com.noob.springbootinit.constant.CommonConstant;
import com.noob.springbootinit.constant.FileConstant;
import com.noob.springbootinit.constant.UserConstant;
import com.noob.springbootinit.exception.BusinessException;
import com.noob.springbootinit.exception.ThrowUtils;
import com.noob.springbootinit.manager.AiManager;
import com.noob.springbootinit.manager.RedisLimiterManager;
import com.noob.springbootinit.model.dto.chart.ChartAddRequest;
import com.noob.springbootinit.model.dto.chart.ChartEditRequest;
import com.noob.springbootinit.model.dto.chart.ChartQueryRequest;
import com.noob.springbootinit.model.dto.chart.ChartUpdateRequest;
import com.noob.springbootinit.model.dto.file.UploadFileRequest;
import com.noob.springbootinit.model.entity.Chart;
import com.noob.springbootinit.model.entity.User;
import com.noob.springbootinit.model.enums.FileUploadBizEnum;
import com.noob.springbootinit.model.vo.BiResponse;
import com.noob.springbootinit.model.vo.ChartVO;
import com.noob.springbootinit.service.ChartService;
import com.noob.springbootinit.service.UserService;
import com.noob.springbootinit.utils.ExcelUtils;
import com.noob.springbootinit.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

import com.noob.springbootinit.model.dto.chart.GenChartByAiRequest;

/**
 * 图表接口
 */
@RestController
@RequestMapping("/chart")
@Slf4j
public class ChartController {

    @Resource
    private ChartService chartService;

    @Resource
    private UserService userService;

    // ai服务
    @Resource
    private AiManager aiManager;

    // 接口限流器
    @Resource
    private RedisLimiterManager redisLimiterManager;

    // 异步处理线程池(自动注入一个线程池实例)
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;


    // region 增删改查

    /**
     * 创建
     *
     * @param chartAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addChart(@RequestBody ChartAddRequest chartAddRequest, HttpServletRequest request) {
        if (chartAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartAddRequest, chart);
        User loginUser = userService.getLoginUser(request);
        chart.setUserId(loginUser.getId());
        boolean result = chartService.save(chart);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newChartId = chart.getId();
        return ResultUtils.success(newChartId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteChart(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldChart.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = chartService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param chartUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateChart(@RequestBody ChartUpdateRequest chartUpdateRequest) {
        if (chartUpdateRequest == null || chartUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartUpdateRequest, chart);
        long id = chartUpdateRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Chart> getChartVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = chartService.getById(id);
        if (chart == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(chart);
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param chartQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Chart>> listChartByPage(@RequestBody ChartQueryRequest chartQueryRequest) {
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size), getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param chartQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<Chart>> listMyChartByPage(@RequestBody ChartQueryRequest chartQueryRequest, HttpServletRequest request) {
        if (chartQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        chartQueryRequest.setUserId(loginUser.getId());
        long current = chartQueryRequest.getCurrent();
        long size = chartQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Chart> chartPage = chartService.page(new Page<>(current, size), getQueryWrapper(chartQueryRequest));
        return ResultUtils.success(chartPage);
    }

    // endregion


    /**
     * 编辑（用户）
     *
     * @param chartEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editChart(@RequestBody ChartEditRequest chartEditRequest, HttpServletRequest request) {
        if (chartEditRequest == null || chartEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Chart chart = new Chart();
        BeanUtils.copyProperties(chartEditRequest, chart);
        User loginUser = userService.getLoginUser(request);
        long id = chartEditRequest.getId();
        // 判断是否存在
        Chart oldChart = chartService.getById(id);
        ThrowUtils.throwIf(oldChart == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldChart.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = chartService.updateById(chart);
        return ResultUtils.success(result);
    }

    /**
     * 获取查询包装类
     *
     * @param chartQueryRequest
     * @return
     */
    private QueryWrapper<Chart> getQueryWrapper(ChartQueryRequest chartQueryRequest) {
        QueryWrapper<Chart> queryWrapper = new QueryWrapper<>();
        if (chartQueryRequest == null) {
            return queryWrapper;
        }
        Long id = chartQueryRequest.getId();
        String name = chartQueryRequest.getName();
        String goal = chartQueryRequest.getGoal();
        String chartType = chartQueryRequest.getChartType();
        Long userId = chartQueryRequest.getUserId();
        String sortField = chartQueryRequest.getSortField();
        String sortOrder = chartQueryRequest.getSortOrder();

        queryWrapper.eq(id != null && id > 0, "id", id);
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(goal), "goal", goal);
        queryWrapper.eq(StringUtils.isNotBlank(chartType), "chartType", chartType);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        return queryWrapper;
    }

    /**
     * 智能分析（异步）
     *
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/genChartByAiAsync")
    public BaseResponse<BiResponse> genChartByAiAsync(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        // 文件信息校验（校验文件后缀、大小等基本信息，防止文件上传漏洞攻击）
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        // 校验文件大小，如果文件大于1兆则抛出异常，提示文件超过1M
        final long ONE_MB = 1024 * 1024;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR,"文件超过1M");
        // 检验文件后缀（一般是xxx.csv，获取到.后缀），可借助FileUtil工具类的getSuffix方法获取
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList("xlsx","xls"); // ".png",".csv",".jpg",".svg","webp","jpeg"
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix),ErrorCode.PARAMS_ERROR,"文件后缀格式非法");

        // 获取当前登陆用户
        User loginUser = userService.getLoginUser(request);
        long biModelId = CommonConstant.BI_MODEL_ID;

        // 引入限流判断
        redisLimiterManager.doRateLimit("genChartByAi_"+loginUser.getId());

        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        // 压缩后的数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");


        // --------------------------------- start 异步处理逻辑 ------------------------------
        // 1.先将图标数据保存到数据库中
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        // 数据库插入时，还没生成结果（先插入数据，异步调用AI接口生成图表信息），将图表任务状态设置为排队中
//        chart.setGenChart(genChart);
//        chart.setGenResult(genResult);
        chart.setStatus("wait");
        chart.setUserId(loginUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");

        // 2.在最终的返回结果前提交一个任务（todo 建议处理任务队列满了后，抛异常的情况，因为提交任务报错了，前端会返回异常）
        CompletableFuture.runAsync(()->{
            // 2.1 修改图表任务状态为执行中
            Chart updatedChart = new Chart();
            updatedChart.setStatus("running");
            boolean updatedOp = chartService.updateById(updatedChart);
            if(!updatedOp){
                handleChartUpdateError(chart.getId(),"更新图表【执行中】状态失败");
                return ;
            }
           // 2.2 异步调用AI接口
            String result = aiManager.doChat(biModelId, userInput.toString());
            // 此处分隔符以设定为参考
            String[] splits = result.split("【【【【【"); // 【【【【【
            if (splits.length < 3) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
            }
            String genChart = splits[1].trim();
            String genResult = splits[2].trim();

            // 2.3 AI结果调用成功后，更新任务状态，并将AI结果进行封装
            Chart updatedChartResult = new Chart();
            updatedChartResult.setId(chart.getId());
            updatedChartResult.setGenChart(genChart);
            updatedChartResult.setGenResult(genResult);
            updatedChartResult.setStatus("succeed");
            boolean updatedResOp = chartService.updateById(updatedChartResult);
            if(!updatedResOp){
                handleChartUpdateError(chart.getId(),"更新图表【成功】状态失败");
            }
        },threadPoolExecutor);
        // --------------------------------- end 异步处理逻辑 ------------------------------
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);
    }

    /**
     * 处理更新图表执行中状态失败方法
     * @param chartId
     * @param execMessage
     */
    private void handleChartUpdateError(Long chartId, String execMessage) {
        Chart updatedChartResult = new Chart();
        updatedChartResult.setId(chartId);
        updatedChartResult.setStatus("failed");
        updatedChartResult.setExecMessage(execMessage);
        boolean updatedResOp = chartService.updateById(updatedChartResult);
        if(!updatedResOp){
            log.error("更新图表【失败】状态失败"+chartId+","+execMessage);
        }
    }


    /**
     * 智能分析接口（同步：校验=》限流=》构造用户输入、调用AI）
     * @param multipartFile
     * @param genChartByAiRequest
     * @param request
     * @return
     */
    @PostMapping("/genChartByAiSync")
    public BaseResponse<BiResponse> genChartByAiSync(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");


        /**
         * 文件信息校验（校验文件后缀、大小等基本信息，防止文件上传漏洞攻击）
         */
        long size = multipartFile.getSize();
        String originalFilename = multipartFile.getOriginalFilename();
        // 校验文件大小，如果文件大于1兆则抛出异常，提示文件超过1M
        final long ONE_MB = 1024 * 1024;
        ThrowUtils.throwIf(size > ONE_MB, ErrorCode.PARAMS_ERROR,"文件超过1M");
        // 检验文件后缀（一般是xxx.csv，获取到.后缀），可借助FileUtil工具类的getSuffix方法获取
        String suffix = FileUtil.getSuffix(originalFilename);
        final List<String> validFileSuffixList = Arrays.asList(".xlsx",".xls"); // ".png",".csv",".jpg",".svg","webp","jpeg"
        ThrowUtils.throwIf(!validFileSuffixList.contains(suffix),ErrorCode.PARAMS_ERROR,"文件后缀格式非法");

        // 获取当前登陆用户
        User loginUser = userService.getLoginUser(request);
        long biModelId = CommonConstant.BI_MODEL_ID;

        // 引入限流判断
        redisLimiterManager.doRateLimit("genChartByAi_"+loginUser.getId());

        // 构造用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("分析需求：").append("\n");

        // 拼接分析目标
        String userGoal = goal;
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += "，请使用" + chartType;
        }
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        // 压缩后的数据
        String csvData = ExcelUtils.excelToCsv(multipartFile);
        userInput.append(csvData).append("\n");

        String result = aiManager.doChat(biModelId, userInput.toString());
        // 此处分隔符以设定为参考
        String[] splits = result.split("【【【【【"); // 【【【【【
        if (splits.length < 3) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误");
        }
        String genChart = splits[1].trim();
        String genResult = splits[2].trim();
        // 插入到数据库
        Chart chart = new Chart();
        chart.setName(name);
        chart.setGoal(goal);
        chart.setChartData(csvData);
        chart.setChartType(chartType);
        chart.setGenChart(genChart);
        chart.setGenResult(genResult);
        chart.setUserId(loginUser.getId());
        boolean saveResult = chartService.save(chart);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "图表保存失败");
        BiResponse biResponse = new BiResponse();
        biResponse.setGenChart(genChart);
        biResponse.setGenResult(genResult);
        biResponse.setChartId(chart.getId());
        return ResultUtils.success(biResponse);
    }

    /*
    public BaseResponse<String> genChartByAi(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        // 用户输入
        StringBuilder userInput = new StringBuilder();
        userInput.append("你是一个数据分析师，接下来我会给你我的分析目标和原始数据，请告诉我分析结论。");
        userInput.append("分析目标：").append(goal).append("\n");

        // 压缩后的数据
        String result = ExcelUtils.excelToCsv(multipartFile);
        userInput.append("数据：").append(result).append("\n");
        return ResultUtils.success(userInput.toString());
    }
     */

    /*
    public BaseResponse<String> genChartByAi(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        // 测试读取文件
        String result = ExcelUtils.excelToCsv(multipartFile);
        return ResultUtils.success(result);
    }
     */
    /*
    public BaseResponse<String> genChartByAi(@RequestPart("file") MultipartFile multipartFile, GenChartByAiRequest genChartByAiRequest, HttpServletRequest request) {
        String name = genChartByAiRequest.getName();
        String goal = genChartByAiRequest.getGoal();
        String chartType = genChartByAiRequest.getChartType();

        // 校验
        ThrowUtils.throwIf(StringUtils.isBlank(goal), ErrorCode.PARAMS_ERROR, "目标为空");
        ThrowUtils.throwIf(StringUtils.isNotBlank(name) && name.length() > 100, ErrorCode.PARAMS_ERROR, "名称过长");

        // 读取用户上传的excel文件，进行处理
        User loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户划分
        String uuid = RandomStringUtils.randomAlphanumeric(8);
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        File file = null;
        try {
            // 返回可访问地址
            return ResultUtils.success("");
        } catch (Exception e) {
//            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
//                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }
    */


}
