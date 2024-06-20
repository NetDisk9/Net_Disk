package com.net.file.controller;


import com.net.api.client.AuthClient;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.FileTypeEnum;
import com.net.common.exception.ChunkErrorException;
import com.net.common.exception.ParameterException;
import com.net.common.util.LongIdUtil;
import com.net.file.constant.FileStatusConstants;
import com.net.file.constant.FileTokenKeyConstants;
import com.net.file.entity.FileData;
import com.net.file.entity.UserFileEntity;
import com.net.file.factory.UserFileEntityFactory;
import com.net.file.pojo.dto.FileUploadDTO;
import com.net.file.service.FileDataService;
import com.net.file.service.FileService;
import com.net.file.support.RateLimit;
import com.net.file.support.RateLimitInputStream;
import com.net.file.support.TaskList;
import com.net.file.util.*;
import com.net.redis.lock.RedissonLockWrapper;
import io.minio.StatObjectResponse;
import io.minio.errors.*;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-05-11
 */
@RestController
@RequestMapping("/file")
public class FileDataController {

    @Resource
    private MinioUtil minioUtil;
    @Resource
    private FileDataService fileDataService;
    @Resource
    private FileService fileService;
    @Resource
    RedissonClient redissonClient;
    @Resource
    RedisTemplate redisTemplate;
    @Resource
    TaskList taskList;
    @Resource
    AuthClient authClient;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseResult upload(
            @RequestPart("chunk") MultipartFile chunk,
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("chunkIndex") Integer chunkIndex) {
        minioUtil.uploadFileChunk(chunk, fileMd5, chunkIndex);
        return ResponseResult.okResult();
    }

    @PostMapping("/check")
    public ResponseResult uploadFast(@Valid @RequestBody FileUploadDTO fileUploadDTO) {
        Long userId = BaseContext.getCurrentId();
        FileData fileData = fileDataService.getFileDataByMd5(fileUploadDTO.getFileMd5());
        if (fileData == null) {
            return ResponseResult.okResult(201, "需要上传");
        }
        //秒传
        UserFileEntity userFile = UserFileEntityFactory.createFileEntity(fileData, fileUploadDTO.getFilePath(), fileUploadDTO.getFileName(), userId);
        fileService.insertFile(userFile);
        return ResponseResult.okResult();
    }

    @Transactional
    @PostMapping("/upload/check")
    public ResponseResult completeUpload(@Valid @RequestBody FileUploadDTO fileUploadDTO) throws Exception {
        Long userId = BaseContext.getCurrentId();
        if (fileUploadDTO.getTotalChunk() == null || fileUploadDTO.getTotalChunk() < 1) {
            throw new ParameterException();
        }
        RedissonLockWrapper redissonLockWrapper = new RedissonLockWrapper(redissonClient, fileUploadDTO.getFileMd5());
        try {
            //上锁
            boolean result = redissonLockWrapper.lock();
            if (!result) {
                throw new RuntimeException();
            }
            FileData fileData = fileDataService.getFileDataByMd5(fileUploadDTO.getFileMd5());
            if (fileData == null) {
                Long fileId = LongIdUtil.createLongId(fileUploadDTO);
                String extName = PathUtil.getPlainExtName(fileUploadDTO.getFileName());
                String fileName = fileId + "." + extName;
                FileTypeEnum fileType = FileTypeEnum.getFileTypeEnumByExtension(extName);
                minioUtil.composeChunk(fileUploadDTO.getFileMd5(), fileUploadDTO.getTotalChunk(), userId, fileName);
                //校验完整性
                StatObjectResponse fileMedaData = minioUtil.getFileMedaData(fileName);
                System.out.println(fileMedaData.etag());
                System.out.println(fileUploadDTO.getFileMd5());
                if (!fileMedaData.etag().equalsIgnoreCase(fileUploadDTO.getFileMd5())) {
                    throw new ChunkErrorException("文件校验失败");
                }
                //文件元信息
                fileData = FileData.builder()
                        .fileCategory(Integer.parseInt(fileType.getTypeCode()))
                        .fileName(fileName)
                        .fileMd5(fileUploadDTO.getFileMd5())
                        .fileUrl(fileName)
                        .fileId(fileId)
                        .fileSize(fileMedaData.size())
                        .delFlag(FileStatusConstants.NORMAL).build();
                fileDataService.save(fileData);
                //异步上传图片到oss
                FileData finalFileData = fileData;
                taskList.addTask(() -> {
                    try {
                        fileDataService.generateImageCover(finalFileData, userId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            //插入用户文件信息
            UserFileEntity userFile = UserFileEntityFactory.createFileEntity(fileData, fileUploadDTO.getFilePath(), fileUploadDTO.getFileName(), userId);
            fileService.insertFile(userFile);
        }
        catch (ChunkErrorException e){
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            //释放锁
            redissonLockWrapper.unlock();
            //删除分片
            taskList.addTask(() -> {
                try {
                    System.out.println("delete begin");
                    minioUtil.deleteChunk(fileUploadDTO.getFileMd5(), fileUploadDTO.getTotalChunk(), userId);
                } catch (Exception ignored) {
                }
            });
        }

        return ResponseResult.okResult();
    }

    /**
     * 分片下载
     * @param range
     * @param userFileId
     * @param response
     * @throws IOException
     * @throws ServerException
     * @throws InsufficientDataException
     * @throws ErrorResponseException
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidResponseException
     * @throws XmlParserException
     * @throws InternalException
     */
    @GetMapping("/chunk/download")
    public void downLoadChunk(@RequestHeader(name = "Range",required = true) String range,@RequestParam("userFileId")Long userFileId ,HttpServletResponse response) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Long userId=BaseContext.getCurrentId();
        long l = System.currentTimeMillis();
        FileData fileData=fileDataService.getFileDataByUserFileId(userFileId,userId,FileStatusConstants.NORMAL);
        if(fileData==null){
            throw new ParameterException();
        }
        HttpHeaderUtil.Range range1=HttpHeaderUtil.parseRange(range);
        if(range1.getBegin()<0){
            throw  new ParameterException("起始偏移量不能低于0");
        }
        StatObjectResponse fileMedaData = minioUtil.getFileMedaData(fileData.getFileUrl());
        range1.setEnd(Math.min(range1.getEnd(), fileData.getFileSize()-1));
        InputStream inputStream = minioUtil.downLoadChunk(range1.getBegin(),range1.getLength(), fileData.getFileUrl());
        ServletOutputStream outputStream = response.getOutputStream();
        //报文头设置
        response.setHeader("Content-type","application/octet-stream;charset=UTF-8");
        response.setHeader("etag",fileMedaData.etag());
        response.setHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileData.getFileName(), StandardCharsets.UTF_8.name()));
        response.setHeader("Content-length",range1.getLength().toString());
        response.setHeader("Content-Range",HttpHeaderUtil.buildContentRange(range1, fileData.getFileSize()));
        response.setHeader("Accept-Ranges","bytes");
        //下载
        RateLimitInputStream rateLimitInputStream;
        //vip
        if(authClient.isVIP()){
            rateLimitInputStream=new RateLimitInputStream(inputStream,redisTemplate, FileTokenKeyConstants.TOKEN_KEY+userId, RateLimit.RateLimitConfig.getVIPRateLimitConfig());
        }
        else
            rateLimitInputStream=new RateLimitInputStream(inputStream,redisTemplate,FileTokenKeyConstants.TOKEN_KEY+userId);
        IOUtils.copy(rateLimitInputStream,outputStream);
        inputStream.close();
        outputStream.close();
        response.flushBuffer();
        long r=System.currentTimeMillis();
        System.out.println("time:"+(r-l));
    }

    /**
     * 小文件下载
     * @param userFileId
     * @param response
     * @throws Exception
     */
    @GetMapping("/download")
    public void downLoad(@RequestParam("userFileId")Long userFileId ,HttpServletResponse response) throws Exception {
        Long userId=BaseContext.getCurrentId();
        long l = System.currentTimeMillis();
        FileData fileData=fileDataService.getFileDataByUserFileId(userFileId,userId,FileStatusConstants.NORMAL);
        if(fileData==null){
            throw new ParameterException();
        }
        StatObjectResponse fileMedaData = minioUtil.getFileMedaData(fileData.getFileUrl());
        InputStream inputStream = minioUtil.getFileInputStream(fileData.getFileUrl());
        ServletOutputStream outputStream = response.getOutputStream();
        response.setHeader("etag",fileMedaData.etag());
        response.setHeader("Content-type","application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode(fileData.getFileName(), StandardCharsets.UTF_8.name()));
        response.setHeader("Content-length",fileData.getFileSize().toString());
        IOUtils.copy(inputStream,outputStream);
        inputStream.close();
        outputStream.close();
        response.flushBuffer();
        long r=System.currentTimeMillis();
        System.out.println("time:"+(r-l));
    }
}
