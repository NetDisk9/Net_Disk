package com.net.file.support;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.io.InputStream;

/**
 * 令牌桶包装的inputstream
 * @author sloth
 * @date 2024/05/31
 */
public class RateLimitInputStream extends InputStream {
    private InputStream inputStream;
    private RateLimit rateLimit;  //令牌桶
    private static final Integer WAIT_TIME=1000;
    public RateLimitInputStream(InputStream inputStream, RedisTemplate redisTemplate,String key) {
        this.inputStream = inputStream;
        this.rateLimit=new RateLimit(redisTemplate,key);
    }
    public RateLimitInputStream(InputStream inputStream, RedisTemplate redisTemplate, String key, RateLimit.RateLimitConfig rateLimitConfig){
        this.inputStream=inputStream;
        this.rateLimit=new RateLimit(redisTemplate,key,rateLimitConfig);
    }
    @Override
    public int read() throws IOException {
        try {
            getTokens(1L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return inputStream.read();
    }

    @Override
    public int read(@NotNull byte[] b) throws IOException {
        Integer tokens = 0;
        try {
            //取令牌
            tokens=getTokens((long) b.length);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return inputStream.read(b,0,tokens);
    }

    /**
     * 取令牌，如果取不到则等待
     * @param size
     * @return int
     * @throws InterruptedException
     */
    private int getTokens(Long size) throws InterruptedException {
        Integer tokens=0;
        while((tokens=rateLimit.getTokens(size))==0){
            Thread.sleep(WAIT_TIME);
            System.out.println("wait");
        }
        return tokens;
    }

}
