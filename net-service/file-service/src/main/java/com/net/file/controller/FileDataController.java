package com.net.file.controller;


import com.net.file.pojo.vo.FileInfo;
import com.net.file.service.StorageService;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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

    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseResult upload(
            @RequestPart("chunk") MultipartFile chunk,
            @RequestParam("fileMd5") String fileMd5,
            @RequestParam("chunkIndex") Integer chunkIndex) {
        minioUtil.uploadFileChunk(chunk, fileMd5, chunkIndex);
        return ResponseResult.okResult();
    }

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
