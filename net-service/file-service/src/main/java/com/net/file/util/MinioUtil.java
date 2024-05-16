package com.net.file.util;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import com.net.common.context.BaseContext;
import com.net.file.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;

/**
 * minio文件操作工具
 */
@Component
public class MinioUtil {

    @Resource
    private MinioClient minioClient;
    @Resource
    private MinioConfig minioConfig;

    /**
     * 创建bucket桶
     */
    @SneakyThrows
    public void createBucket(String bucket){
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    /**
     * 上传文件
     */
    public void uploadFile(InputStream inputStream, String bucket, String objectName) throws Exception {
        minioClient.putObject(PutObjectArgs.builder().bucket(bucket).object(objectName)
                .stream(inputStream, -1, 5242889L).build());
    }

    /**
     * 分片上传
     */
    @SneakyThrows
    public void uploadFileChunk(MultipartFile chunk, String fileMd5, Integer chunkIndex) {
        createBucket(minioConfig.getDefaultBucket()); // 创建默认桶
        String objectName = generateName(fileMd5, BaseContext.getCurrentId(), chunkIndex);
        InputStream inputStream = chunk.getInputStream();
        uploadFile(inputStream, minioConfig.getDefaultBucket(), objectName);
        IoUtil.close(inputStream);
    }

    /**
     * 生成文件存放位置 + 文件名
     */
    private String generateName(String fileMd5, Long currentId, Integer chunkIndex) {
        return fileMd5 + "/" + currentId + "/" + chunkIndex;
    }
}
