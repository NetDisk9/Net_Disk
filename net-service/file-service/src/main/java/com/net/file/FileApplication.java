package com.net.file;

import com.net.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@MapperScan("com.net.file.mapper")
@EnableFeignClients(basePackages = "com.net.api.client", defaultConfiguration = DefaultFeignConfig.class)
public class FileApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class,args);
        System.out.println("=============================>文件服务启动");
    }
}
