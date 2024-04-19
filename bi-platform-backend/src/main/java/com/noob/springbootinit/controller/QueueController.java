package com.noob.springbootinit.controller;

import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @ClassName QueueController
 * @Description TODO
 * @Author holic-x
 * @Date 2024 2024/4/19 22:16
 */
@RestController
@RequestMapping("/queue")
@Slf4j
@Profile({ "dev", "remote" })
public class QueueController {

    // 注入线程池实例
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    // 将任务添加到线程池
    @GetMapping("/add")
    public void add(String name) {
        // 借助CompletableFuture执行异步任务
        CompletableFuture.runAsync(() -> {
            // 打印日志信息（包括任务名称和执行线程的名称）
            log.info("任务执行中：" + name + "，执行人：" + Thread.currentThread().getName());
            try {
                // 让线程休眠10分钟，模拟长时间运行的任务
                Thread.sleep(600000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 异步任务在threadPoolExecutor中执行
        }, threadPoolExecutor);
    }

    // 获取线程池状态信息
    @GetMapping("/get")
    public String get() {
        // 创建Map存储线程池的状态信息
        Map<String, Object> map = new HashMap<>();
        int size = threadPoolExecutor.getQueue().size();
        map.put("队列长度", size);
        long taskCount = threadPoolExecutor.getTaskCount();
        map.put("任务总数", taskCount);
        long completedTaskCount = threadPoolExecutor.getCompletedTaskCount();
        map.put("已完成任务数", completedTaskCount);
        int activeCount = threadPoolExecutor.getActiveCount();
        map.put("正在工作的线程数", activeCount);
        // 将map转换为JSON字符串并返回
        return JSONUtil.toJsonStr(map);
    }
}