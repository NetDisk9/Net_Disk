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
import com.net.file.util.MinioUtil;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
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
