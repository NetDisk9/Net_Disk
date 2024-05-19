package com.net.file.controller;


import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.FileTypeEnum;
import com.net.common.exception.ParameterException;
import com.net.common.util.LongIdUtil;
import com.net.file.constant.FileStatusConstants;
import com.net.file.entity.FileData;
import com.net.file.entity.UserFileEntity;
import com.net.file.factory.UserFileEntityFactory;
import com.net.file.pojo.dto.FileUploadDTO;
import com.net.file.service.FileDataService;
import com.net.file.service.FileService;
import com.net.file.support.TaskList;
import com.net.file.util.*;
import com.net.redis.lock.RedissonLockWrapper;
import org.redisson.api.RedissonClient;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
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
    TaskList taskList;

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
                FileUtil.FileMetaData metaData = FileUtil.getMetaData(minioUtil.getFileInputStream(fileName));
                if (!Objects.equals(fileUploadDTO.getFileMd5(), metaData.getMd5())) {
                    throw new Exception("文件校验失败");
                }
                //文件元信息
                fileData = FileData.builder()
                        .fileCategory(Integer.parseInt(fileType.getTypeCode()))
                        .fileName(fileName)
                        .fileMd5(fileUploadDTO.getFileMd5())
                        .fileUrl(fileName)
                        .fileId(fileId)
                        .fileSize(metaData.getFileSize())
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
}
