package com.net.file;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.net.file.mapper")
public class FileApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileApplication.class,args);
        System.out.println("=============================>文件服务启动");
    }
}
