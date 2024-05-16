package com.net.file.config;

import com.net.file.util.AliOssUtil;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 配置类，用于创建AliOssUtil对象
 */
@Configuration
@Slf4j
public class OssConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public AliOssUtil aliOssUtil(AliOssConfig aliOssConfig) {
        log.info("开始创建阿里云文件上传工具类对象：{}", aliOssConfig);
        return new AliOssUtil(aliOssConfig.getEndpoint(),
                aliOssConfig.getAccessKeyId(),
                aliOssConfig.getAccessKeySecret(),
                aliOssConfig.getBucketName());
    }

    /**
     * 构造minioClient
     */
    @Bean
    public MinioClient getMinioClient(MinioConfig minioConfig) {
        log.info("开始创建minio文件上传工具类对象：{}", minioConfig);
        return MinioClient.builder()
                .endpoint(minioConfig.getUrl())
                .credentials(minioConfig.getAccessKey(), minioConfig.getSecretKey())
                .build();
    }
}
