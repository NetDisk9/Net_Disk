package com.net.file.support;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

/**
 * 令牌桶
 * @author sloth
 * @date 2024/05/31
 */
public class RateLimit {
    private RedisTemplate redisTemplate;
    private RateLimitConfig rateLimitConfig;
    private String key;
    private String timeKey;
    private static final String TIME_SUFFIX=":time";
    private DefaultRedisScript<Long> redisScript;

    public RateLimit(RedisTemplate redisTemplate, String key,RateLimitConfig rateLimitConfig) {
        this.redisTemplate = redisTemplate;
        this.key = key;
        this.timeKey=key+TIME_SUFFIX;
        this.rateLimitConfig=new RateLimitConfig();
        this.redisScript=new DefaultRedisScript<>();
        this.redisScript.setResultType(Long.class);
        this.redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("RateLimit.lua")));
    }
    public RateLimit(RedisTemplate redisTemplate, String key) {
        this(redisTemplate,key,RateLimitConfig.getNormalRateLimitConfig());
    }
    public Integer getTokens(){
        Long tokens= (Long) redisTemplate.execute(redisScript, List.of(key,timeKey),rateLimitConfig.getTokenInterval(),rateLimitConfig.getTokenSize(),rateLimitConfig.getTokenCount());
        return tokens.intValue();
    }
    //取令牌
    public Integer getTokens(Long size){
        Long tokens= (Long) redisTemplate.execute(redisScript, List.of(key,timeKey),rateLimitConfig.getTokenInterval(),rateLimitConfig.getTokenSize(),size);
//        System.out.println(size+" "+tokens);
        return tokens.intValue();
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RateLimitConfig{
        private Long tokenSize=300*1024L;
        private Long tokenCount=1024L;
        private Integer tokenInterval=2;
        public static RateLimitConfig getVIPRateLimitConfig(){
            return new RateLimitConfig(100*1024*1024L,1024L,60);
        }
        public static RateLimitConfig getNormalRateLimitConfig(){
            return new RateLimitConfig();
        }
    }
}
