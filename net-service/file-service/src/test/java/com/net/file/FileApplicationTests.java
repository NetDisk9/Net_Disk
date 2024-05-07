package com.net.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.net.file.entity.UserFileEntity;
import com.net.file.mapper.FileMapper;
import com.net.file.service.FileService;
import com.net.file.util.RegexUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class FileApplicationTests {
    @Resource
    FileService fileService;
    @Resource
    FileMapper fileMapper;
//    @Test
//    public void testImg(){
//        System.out.println(RegexUtil.checkImageValid("jpg"));
//    }
//    @Test
//    public void testPath()
//    {
//        System.out.println(fileService.list(new LambdaQueryWrapper<UserFileEntity>().eq(UserFileEntity::getPid,null)));
//    }

}
