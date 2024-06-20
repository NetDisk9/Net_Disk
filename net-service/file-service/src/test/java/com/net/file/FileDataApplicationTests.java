package com.net.file;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.net.api.client.AuthClient;
import com.net.common.context.BaseContext;
import com.net.common.util.LongIdUtil;
import com.net.file.config.MinioConfig;
import com.net.file.constant.DirConstants;
import com.net.file.constant.FileStatusConstants;
import com.net.file.controller.FileController;
import com.net.file.controller.FileDataController;
import com.net.file.entity.FileData;
import com.net.file.entity.UserFileEntity;
import com.net.file.mapper.FileMapper;
import com.net.file.pojo.dto.FileUploadDTO;
import com.net.file.service.FileDataService;
import com.net.file.service.FileService;
import com.net.file.service.Impl.FileServiceImpl;
import com.net.file.service.ShareService;
import com.net.file.util.FileUtil;
import com.net.file.util.MinioUtil;
import com.net.file.util.RegexUtil;
import com.net.redis.constant.RedisConstants;
import com.net.redis.utils.RedisUtil;
import io.minio.MinioClient;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class FileDataApplicationTests {
        @Resource
        FileController fileController;
//    @Resource
//    FileDataService fileDataService;
//    @Resource
//    FileMapper fileMapper;
//    @Resource
//    MinioConfig minioConfig;
//    @Resource
//    FileDataController fileDataController;
//    @Resource
//    MinioClient minioClient;
//    @Resource
//    AuthClient authClient;
//    @Resource
//    ShareService shareService;
    @Resource
    RedisUtil redisUtil;
    @Resource
    FileDataService fileDataService;

    //    @Test
//    public void testPath() {
//        AntPathMatcher antPathMatcher = new AntPathMatcher();
//        System.out.println(antPathMatcher.match("/username/{username}/aaa", "/username/555/aaa"));
//    }
//
//    @Test
//    public void testLinkFileDatabase() {
//        BaseContext.setCurrentId(1L);
//        FileUploadDTO fileUploadDTO = new FileUploadDTO();
//        fileUploadDTO.setFileName("test.txt");
//        fileUploadDTO.setFileMd5("1");
//        fileUploadDTO.setFilePath("");
//        fileDataController.uploadFast(fileUploadDTO);
////        fileDataController.uploadFast(fileUploadDTO);
//
//    }
//
//    @Test
//    public void uploadCompleteTest() throws Exception {
//        FileUploadDTO fileUploadDTO = new FileUploadDTO();
//        fileUploadDTO.setFileName("Anno.mp4");
//        fileUploadDTO.setFileMd5("77777777888qwe");
//        fileUploadDTO.setFilePath("/video/Anno.mp4");
//        fileUploadDTO.setTotalChunk(3);
//        BaseContext.setCurrentId(1L);
//        fileDataController.completeUpload(fileUploadDTO);
//    }
//
//    @Test
//    public void testUpload() throws Exception {
//        File file = new File("D:\\Desktop\\大三下学习\\软件工程实训\\后端代码\\Net_Disk\\net-service\\file-service\\src\\test\\java\\com\\net\\file\\text.txt");
//
//        System.out.println("testUpload==>");
//        System.out.println(DigestUtil.md5Hex(file));
//        System.out.println("==");
//        minioClient.uploadObject(
//                UploadObjectArgs.builder()
//                        .bucket(minioConfig.getDefaultBucket())//桶
//                        .filename(file.getAbsolutePath()) //指定本地文件路径
//                        .object("test/test.txt")//对象名 放在子目录下
//                        .build()
//        );
//
//        StatObjectResponse statObjectResponse = minioClient.statObject(
//                StatObjectArgs.builder()
//                        .object("test/test.txt")
//                        .bucket(minioConfig.getDefaultBucket())
//                        .build()
//        );
//        System.out.println(statObjectResponse.etag());
//        System.out.println(statObjectResponse.size());
//        System.out.println("testUpload==>");
//    }
//
//    @Test
//    public void testGetMd5() throws Exception {
//        File file = new File("D:\\Desktop\\大三下学习\\软件工程实训\\后端代码\\Net_Disk\\net-service\\file-service\\src\\test\\java\\com\\net\\file\\text.txt");
//        FileInputStream fileInputStream = new FileInputStream(file);
//        FileUtil.FileMetaData metaData = FileUtil.getMetaData(fileInputStream);
//        System.out.println(metaData.getMd5());
//        StatObjectArgs object = StatObjectArgs.builder().object("1792765415473094658.png")
//                .bucket(minioConfig.getDefaultBucket()).build();
//        StatObjectResponse statObjectResponse = minioClient.statObject(object);
//        System.out.println(statObjectResponse.etag());
//        System.out.println(statObjectResponse.size());
//    }
//    @Test
//    public void testGetFileData(){
//        FileData fileData = fileDataService.getFileDataByUserFileId(1791346282007633922L, 1768546042247323649L, 0);
//        System.out.println(fileData);
//    }
    @Test
    public void testRedis() {
//        redisUtil.set(RedisConstants.FILE_SHARE_CHECK_KEY +"11151","1145A",RedisConstants.FILE_SHARE_TTL);
        Object redisCount = redisUtil.get(RedisConstants.FILE_SHARE_COUNT + "11151");
        if (redisCount == null) redisUtil.set(RedisConstants.FILE_SHARE_COUNT + "11151", 1);
        Object redisCount1 = redisUtil.get(RedisConstants.FILE_SHARE_COUNT + "11151");
        System.out.println(redisCount1);
    }

    @Test
    public void testGetFileData() {
        BaseContext.setCurrentId(1768546042247323649L);
        fileController.removeFile2Recycle(List.of(1801267931095449602L));
    }


}
