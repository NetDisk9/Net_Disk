package com.net.file.service.Impl;

import com.net.file.pojo.vo.FileInfo;
import com.net.file.service.StorageService;
import com.net.file.util.MinioUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.List;

@Service

/**
 * minioIO存储适配器
 */
public class MinioStorageServiceImpl implements StorageService {

    @Resource
    private MinioUtil minioUtil;

    /**
     * minioUrl
     */
    @Value("${minio.url}")
    private String url;

    @Override
    @SneakyThrows
    public void createBucket(String bucket) {
        minioUtil.createBucket(bucket);
    }

    @Override
    @SneakyThrows
    public void uploadFile(MultipartFile uploadFile, String bucket, String objectName) {
        minioUtil.createBucket(bucket);
        if (objectName != null) {
            minioUtil.uploadFile(uploadFile.getInputStream(), bucket, objectName + "/" + uploadFile.getOriginalFilename());
        } else {
            minioUtil.uploadFile(uploadFile.getInputStream(), bucket, uploadFile.getOriginalFilename());
        }
    }

    @Override
    @SneakyThrows
    public List<String> getAllBucket() {
        return minioUtil.getAllBucket();
    }

    @Override
    @SneakyThrows
    public List<FileInfo> getAllFile(String bucket) {
        return minioUtil.getAllFile(bucket);
    }

    @Override
    @SneakyThrows
    public InputStream downLoad(String bucket, String objectName) {
        return minioUtil.downLoad(bucket, objectName);
    }

    @Override
    @SneakyThrows
    public void deleteBucket(String bucket) {
        minioUtil.deleteBucket(bucket);
    }

    @Override
    @SneakyThrows
    public void deleteObject(String bucket, String objectName) {
        minioUtil.deleteObject(bucket, objectName);
    }

    @Override
    @SneakyThrows
    public String getUrl(String bucket, String objectName) {
        return url + "/" + bucket + "/" + objectName;
    }

    @SneakyThrows
    public String getPreviewFileUrl(String bucketName, String objectName) {
        return minioUtil.getPreviewFileUrl(bucketName, objectName);
    }
}
