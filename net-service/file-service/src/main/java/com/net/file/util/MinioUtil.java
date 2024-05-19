package com.net.file.util;

import cn.hutool.core.io.IoUtil;
import com.net.common.context.BaseContext;
import com.net.file.config.MinioConfig;
import io.minio.*;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
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
    public void createBucket(String bucket) {
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
     * 合并分片
     *
     * @param fileMd5
     * @param totalChunk
     * @param userId
     * @param filePath
     * @throws Exception
     */
    public void composeChunk(String fileMd5, int totalChunk, Long userId, String filePath) throws Exception {
        List<ComposeSource> sources = Stream.iterate(0, num -> ++num)
                .limit(totalChunk)
                .map(num ->
                        ComposeSource.builder()
                                .bucket(minioConfig.getDefaultBucket())
                                .object(generateName(fileMd5, userId, num))
                                .build())
                .collect(Collectors.toList());
        ComposeObjectArgs args = ComposeObjectArgs.builder()
                .sources(sources)
                .bucket(minioConfig.getDefaultBucket())
                .object(filePath)
                .build();
        minioClient.composeObject(args);
    }

    /**
     * 取文件输入流
     *
     * @param filePath
     * @return {@link InputStream }
     * @throws Exception
     */
    public InputStream getFileInputStream(String filePath) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .object(filePath)
                .bucket(minioConfig.getDefaultBucket())
                .build();
        return minioClient.getObject(args);
    }

    /**
     * 取分片输入流
     *
     * @param fileMd5
     * @param userId
     * @param chunkIndex
     * @return {@link InputStream }
     * @throws Exception
     */
    public InputStream getChunkInputStream(String fileMd5, Long userId, int chunkIndex) throws Exception {
        return getFileInputStream(generateName(fileMd5, userId, chunkIndex));
    }

    /**
     * 删除分片
     *
     * @param fileMd5
     * @param totalChunk
     * @param userId
     * @throws Exception
     */
    public void deleteChunk(String fileMd5, int totalChunk, Long userId) throws Exception {
        List<DeleteObject> list = Stream.iterate(0, num -> ++num)
                .limit(totalChunk)
                .map(num -> new DeleteObject(generateName(fileMd5, userId, num)))
                .collect(Collectors.toList());
        System.out.println(list.size());
        RemoveObjectsArgs args = RemoveObjectsArgs.builder()
                .bucket(minioConfig.getDefaultBucket())
                .objects(list)
                .build();
        Iterable<Result<DeleteError>> results = minioClient.removeObjects(args);
        for (Result<DeleteError> result : results) {
            System.out.println("delete");
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
