package com.net.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.net.common.context.BaseContext;
import com.net.common.util.LongIdUtil;
import com.net.file.config.MinioConfig;
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
import com.net.file.util.FileUtil;
import com.net.file.util.MinioUtil;
import com.net.file.util.RegexUtil;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;

@SpringBootTest
public class FileDataApplicationTests {
    @Resource
    FileService fileService;
    @Resource
    FileDataService fileDataService;
    @Resource
    FileMapper fileMapper;
    @Resource
    MinioConfig minioConfig;
    @Resource
    FileDataController fileDataController;
    @Resource
    MinioClient minioClient;
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
        fileUploadDTO.setFilePath("");
        fileDataController.uploadFast(fileUploadDTO);
//        fileDataController.uploadFast(fileUploadDTO);

    }
    @Test
    public void uploadCompleteTest() throws Exception {
        FileUploadDTO fileUploadDTO=new FileUploadDTO();
        fileUploadDTO.setFileName("Anno.mp4");
        fileUploadDTO.setFileMd5("77777777888qwe");
        fileUploadDTO.setFilePath("/video/Anno.mp4");
        fileUploadDTO.setTotalChunk(3);
        BaseContext.setCurrentId(1L);
        fileDataController.completeUpload(fileUploadDTO);
    }
    @Test
    public void testGetMd5() throws Exception{
        File file=new File("C:\\Users\\sloth\\Downloads\\1792765415473094658.png");
        FileInputStream fileInputStream=new FileInputStream(file);
        FileUtil.FileMetaData metaData = FileUtil.getMetaData(fileInputStream);
        System.out.println(metaData.getMd5());
        StatObjectArgs object = StatObjectArgs.builder().object("1792765415473094658.png")
                .bucket(minioConfig.getDefaultBucket()).build();
        StatObjectResponse statObjectResponse = minioClient.statObject(object);
        System.out.println(statObjectResponse.etag());
        System.out.println(statObjectResponse.size());
    }
}
