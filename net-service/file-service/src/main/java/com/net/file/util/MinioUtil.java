package com.net.file.util;

import cn.hutool.core.io.IoUtil;
import com.net.common.context.BaseContext;
import com.net.file.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public void composeChunk(String fileMd5,int totalChunk,Long userId,String filePath) throws Exception{
        List<ComposeSource> sources = Stream.iterate(0, num -> ++num).limit(totalChunk).map(num -> {
            return ComposeSource.builder()
                    .bucket(minioConfig.getDefaultBucket())
                    .object(String.valueOf(num))
                    .build();
        }).collect(Collectors.toList());
        ComposeObjectArgs args= ComposeObjectArgs.builder()
                .sources(sources)
                .bucket(minioConfig.getDefaultBucket())
                .object(filePath)
                .build();
        minioClient.composeObject(args);
    }
    public InputStream getFileInputStream(String filePath) throws Exception{
        GetObjectArgs args= GetObjectArgs.builder()
                .object(filePath)
                .bucket(minioConfig.getDefaultBucket())
                .build();
        return minioClient.getObject(args);
    }
    public InputStream getChunkInputStream(String fileMd5,Long userId,int chunkIndex) throws Exception{
        return getFileInputStream(generateName(fileMd5,userId,chunkIndex));
    }
    public void deleteChunk(String fileMd5,int totalChunk,Long userId)  throws Exception{
        List<DeleteObject> list = Stream.iterate(0, num -> ++num).limit(totalChunk).map(num -> {
            return new DeleteObject(generateName(fileMd5,userId,num));
        }).collect(Collectors.toList());
        RemoveObjectsArgs args= RemoveObjectsArgs.builder()
                .bucket(minioConfig.getDefaultBucket())
                .objects(list)
                .build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(args);
        for (Result<DeleteError> result : results) {
            result.get();
        }
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
