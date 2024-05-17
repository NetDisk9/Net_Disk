package com.net.file.controller;


import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.exception.ParameterException;
import com.net.common.util.LongIdUtil;
import com.net.file.entity.FileData;
import com.net.file.entity.UserFileEntity;
import com.net.file.factory.UserFileEntityFactory;
import com.net.file.pojo.dto.FileUploadDTO;
import com.net.file.pojo.vo.FileInfo;
import com.net.file.service.FileDataService;
import com.net.file.service.FileService;
import com.net.file.util.Md5Util;
import com.net.file.util.MinioUtil;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 *  前端控制器
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
    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseResult upload(
            @RequestPart("chunk") MultipartFile chunk,
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("chunkIndex") Integer chunkIndex) {
        minioUtil.uploadFileChunk(chunk, fileMd5, chunkIndex);
        return ResponseResult.okResult();
    }

    @PostMapping("/check")
    public ResponseResult uploadFast(@Valid @RequestBody FileUploadDTO fileUploadDTO){
        Long userId= BaseContext.getCurrentId();
        FileData fileData = fileDataService.getFileDataByMd5(fileUploadDTO.getFileMd5());
        if(fileData==null){
            return ResponseResult.okResult(201,"需要上传");
        }
        UserFileEntity userFile= UserFileEntityFactory.createFileEntity(fileData, fileUploadDTO.getFilePath(), fileUploadDTO.getFileName(), userId);
        fileService.insertFile(userFile);
        return ResponseResult.okResult();
    }
    @PostMapping("/upload/check")
    ResponseResult completeUpload(@Valid @RequestBody FileUploadDTO fileUploadDTO) throws Exception{
        Long userId=BaseContext.getCurrentId();
        if(fileUploadDTO.getTotalChunk()==null||fileUploadDTO.getTotalChunk()<1){
            throw new ParameterException();
        }
        try{
            //todo 上锁
            FileData fileData = fileDataService.getFileDataByMd5(fileUploadDTO.getFileMd5());
            if(fileData==null){
                Long fileId= LongIdUtil.createLongId(fileUploadDTO);
                String fileName=String.valueOf(fileId);
                minioUtil.composeChunk(fileUploadDTO.getFileMd5(), fileUploadDTO.getTotalChunk(),userId,fileName);
                //todo 校验完整性
                if(!Objects.equals(fileUploadDTO.getFileMd5(), Md5Util.getMD5(minioUtil.getFileInputStream(fileName)))){
                    throw new Exception("文件校验失败");
                }
                InputStream imageInputstream;
                //todo 生成视频缩略图
                //todo 生成图片缩略图
                //todo 异步上传图片到oss
                
                //todo 文件元信息
            }
            //todo 插入文件元信息
        }catch (Exception e){
            throw new RuntimeException();
        }finally {
            //todo 释放锁
            //删除分片
            minioUtil.deleteChunk(fileUploadDTO.getFileMd5(), fileUploadDTO.getTotalChunk(),userId);
        }

        return ResponseResult.okResult();
    }
}
