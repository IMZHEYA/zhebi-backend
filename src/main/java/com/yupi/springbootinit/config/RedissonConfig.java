package com.yupi.springbootinit.config;

import io.lettuce.core.RedisClient;
import io.swagger.models.auth.In;
import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("spring.redis")
@Data
public class RedissonConfig {

    private Integer database;

    private String host;

    private Integer port;

    // spring启动时，会自动创建一个RedissonClient对象
    @Bean
    public RedissonClient getRedissonClient() {
        // 1.创建配置对象
        Config config = new Config();
        // 2. 添加单机Redisson配置
        config.useSingleServer()
        // 设置数据库
                .setDatabase(1)
        //设置redis的地址
                .setAddress("redis://" + host + ":" + port);
        //3..创建Redisson实例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }

}
