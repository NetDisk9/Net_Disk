package com.net.redis.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
@Data
@Component
public class RedissonClientConfig {
    @Value("${spring.redis.redisson.address}")
    private String address;
    @Value("${spring.redis.redisson.password}")
    private String password;
}
