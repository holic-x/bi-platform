package com.noob.springbootinit.manager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @ClassName Huh-x
 * @Description TODO
 * @Author Huh-x
 * @Date 2024 2024/4/19 16:15
 */
@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    private RedisLimiterManager redisLimiterManager;

    @Test
    void doRateLimit() throws InterruptedException {
        // 模拟用户操作
        String userId = "1";
        // 瞬间执行2次，每成功一次，就打印成功
        for(int i=0;i<2;i++){
            redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }
        // 睡1s
        Thread.sleep(1000);
        // 瞬间执行2次，每成功一次，就打印成功
        for(int i=0;i<5;i++){
            redisLimiterManager.doRateLimit(userId);
            System.out.println("成功");
        }
    }
}