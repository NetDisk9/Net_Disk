package com.net.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.net.common.context.BaseContext;
import com.net.common.util.LongIdUtil;
import com.net.file.constant.DirConstants;
import com.net.file.constant.FileStatusConstants;
import com.net.file.controller.FileController;
import com.net.file.controller.FileDataController;
import com.net.file.entity.UserFileEntity;
import com.net.file.mapper.FileMapper;
import com.net.file.pojo.dto.FileUploadDTO;
import com.net.file.service.FileDataService;
import com.net.file.service.FileService;
import com.net.file.service.Impl.FileServiceImpl;
import com.net.file.util.RegexUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;

@SpringBootTest
public class FileDataApplicationTests {
    @Resource
    FileService fileService;
    @Resource
    FileDataService fileDataService;
    @Resource
    FileMapper fileMapper;
    @Resource
    FileDataController fileDataController;
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
    public void testPath(){
        AntPathMatcher antPathMatcher=new AntPathMatcher();
        System.out.println(antPathMatcher.match("/username/{username}/aaa", "/username/555/aaa"));
    }
    @Test
    public void testLinkFileDatabase(){
        BaseContext.setCurrentId(1L);
        FileUploadDTO fileUploadDTO=new FileUploadDTO();
        fileUploadDTO.setFileName("test.txt");
        fileUploadDTO.setFileMd5("1");
        fileUploadDTO.setFilePath("/www/wewe/test.txt");
        fileDataController.uploadFast(fileUploadDTO);
        fileDataController.uploadFast(fileUploadDTO);

    }

}
