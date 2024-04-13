package com.yupi.springbootinit.manager;

import com.yupi.springbootinit.common.ErrorCode;
import com.yupi.springbootinit.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RedisLimiterManager {
    @Resource
    private RedissonClient redissonClient;

    public void doRateLimit(String key){
        // 创建一个名称为rateLimiter的限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // 限流器的统计规则(每秒2个请求;连续的请求,最多只能有1个请求被允许通过)
        // RateType.OVERALL表示速率限制作用于整个令牌桶,即限制所有请求的速率
        rateLimiter.trySetRate(RateType.OVERALL,2,1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canop = rateLimiter.tryAcquire(1);
        // 如果没有令牌,还想执行操作,就抛出异常
        if(!canop){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }

    }
}
