package com.net.user;

import com.net.api.config.DefaultFeignConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("com.net.user.mapper")
@EnableFeignClients(basePackages = "com.net.api.client", defaultConfiguration = DefaultFeignConfig.class)
@EnableTransactionManagement
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class,args);
        System.out.println("=============================>用户服务启动");
    }
}
