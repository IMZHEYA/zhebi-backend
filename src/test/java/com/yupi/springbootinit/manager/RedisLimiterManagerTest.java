package com.yupi.springbootinit.manager;

import org.apache.ibatis.logging.stdout.StdOutImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisLimiterManagerTest {

    @Resource
    private RedisLimiterManager redisLimiterManager;
    @Test
    void doRateLimit() throws InterruptedException {
        for(int i = 0; i < 2; i++){
            redisLimiterManager.doRateLimit("1");
            System.out.println("成功");
        }
        Thread.sleep(1000);
        for(int i = 0; i < 5; i++){
            redisLimiterManager.doRateLimit("1");
            System.out.println("成功");
        }
    }
}