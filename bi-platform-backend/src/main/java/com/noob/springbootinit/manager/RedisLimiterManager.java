package com.noob.springbootinit.manager;

import com.noob.springbootinit.common.ErrorCode;
import com.noob.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @ClassName Huh-x
 * @Description 专门提供RedisLimiter限流基础服务（通用定义，可适用于一般的项目）
 * @Author Huh-x
 * @Date 2024 2024/4/19 16:02
 */
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 限流惭怍
     * @param key 区分不同的限流器（例如不同用户的ID分别统计）
     */
    public void doRateLimit(String key){
        // 创建一个user_limiter限流器（每秒最多访问2次）
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // 限流器的统计规则（每秒2个请求：连续的请求，最多只能1个请求被允许通过）
        rateLimiter.trySetRate(RateType.OVERALL,2,1, RateIntervalUnit.SECONDS);
        // 每当一个操作访问，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        // 如果没有令牌还想执行操作则抛出异常
        if(!canOp){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }
}
