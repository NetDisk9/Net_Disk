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
    private StorageService storageService;

    @SneakyThrows
    @RequestMapping("/testGetAllBuckets")
    public int testGetAllBuckets() {
        List<String> allBucket = storageService.getAllBucket();
        return allBucket.size();
    }

    @RequestMapping("/uploadFile")
    void uploadFile(MultipartFile uploadFile, String bucket, String objectName) {
        storageService.uploadFile(uploadFile, bucket, objectName);
    }

    @RequestMapping("/getAllFile")
    List<FileInfo> getAllFile(String bucket) {
        return storageService.getAllFile(bucket);
    }

    @RequestMapping("/getUrl")
    String getUrl(String bucket, String objectName) {
        return storageService.getUrl(bucket, objectName);
    }

    @RequestMapping("/getPreviewFileUrl")
    String getPreviewFileUrl(String bucket, String objectName) {
        return storageService.getPreviewFileUrl(bucket, objectName);
    }
}
