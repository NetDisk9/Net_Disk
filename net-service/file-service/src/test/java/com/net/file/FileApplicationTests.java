package com.net.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.net.common.util.LongIdUtil;
import com.net.file.constant.DirConstants;
import com.net.file.constant.FileStatusConstants;
import com.net.file.entity.UserFileEntity;
import com.net.file.mapper.FileMapper;
import com.net.file.service.FileService;
import com.net.file.service.Impl.FileServiceImpl;
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
//    public void testPath() throws InterruptedException {
//        UserFileEntity test = UserFileEntity.UserFileEntityFactory.createDirEntity(null, "test", 1L);
//        UserFileEntity userFileEntity = fileService.getFileIdByPath("/test", 1768546042247323649L);
//        System.out.println(userFileEntity);
//    }
    @Test
    public void testSelect(){
        System.out.println(fileMapper.getUserFileByUserFileId(1784617459011162113L));
    }

}
