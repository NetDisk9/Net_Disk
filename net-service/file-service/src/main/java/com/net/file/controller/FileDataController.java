package com.net.file.controller;


import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.file.entity.FileData;
import com.net.file.entity.UserFileEntity;
import com.net.file.factory.UserFileEntityFactory;
import com.net.file.pojo.dto.FileUploadDTO;
import com.net.file.pojo.vo.FileInfo;
import com.net.file.service.FileDataService;
import com.net.file.service.FileService;
import com.net.file.service.StorageService;
import lombok.SneakyThrows;
import lombok.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

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
    private StorageService storageService;
    @Resource
    private FileDataService fileDataService;
    @Resource
    private FileService fileService;

    @RequestMapping("/uploadFile")
    void uploadFile(MultipartFile uploadFile, String bucket, String objectName) {
        storageService.uploadFile(uploadFile, bucket, objectName);
    }
    @PostMapping("/file/check")
    public ResponseResult uploadFast(@Valid FileUploadDTO fileUploadDTO){
        Long userId= BaseContext.getCurrentId();
        FileData fileData = fileDataService.getFIleDataByMd5(fileUploadDTO.getFileMd5());
        if(fileData==null){
            return ResponseResult.okResult(201,"需要上传");
        }
        UserFileEntity userFile= UserFileEntityFactory.createFileEntity(fileData, fileUploadDTO.getFilePath(), fileUploadDTO.getFileName(), userId);
        fileService.insertFile(userFile);
        return ResponseResult.okResult();
    }
    @PostMapping("/file/upload/check")
    ResponseResult completeUpload(@Valid FileUploadDTO fileUploadDTO){
        return ResponseResult.okResult();
    }
}
