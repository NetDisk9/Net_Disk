package com.net.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * minio配置管理
 */

@Component
@ConfigurationProperties(prefix = "net.minio")
@Data
public class MinioConfig {
    private String url;
    private String accessKey;
    private String secretKey;
    private String defaultBucket;
}
