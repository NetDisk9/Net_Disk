package com.net.file.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.enums.FileTypeEnum;
import com.net.file.entity.FileData;
import com.net.file.mapper.FileDataMapper;
import com.net.file.service.FileDataService;
import com.net.file.util.AliOssUtil;
import com.net.file.util.ImgTools;
import com.net.file.util.MinioUtil;
import com.net.file.util.PathUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-05-11
 */
@Service
public class FileDataServiceImpl extends ServiceImpl<FileDataMapper, FileData> implements FileDataService {
    @Resource
    FileDataMapper fileDataMapper;
    @Resource
    MinioUtil minioUtil;
    @Resource
    AliOssUtil aliOssUtil;
    @Override
    public FileData getFileDataByMd5(String md5) {
        LambdaQueryWrapper<FileData> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileData::getFileMd5,md5);
        return getOne(queryWrapper);
    }

    @Override
    public void generateImageCover(FileData fileData,Long userId) throws Exception{
        byte[] imageBytes;
        //生成视频缩略图
        FileTypeEnum fileType=FileTypeEnum.getFileTypeEnumByCode(String.valueOf(fileData.getFileCategory()));
        if(fileType==FileTypeEnum.VIDEO){
            imageBytes= ImgTools.randomGrabberFFmpegVideoImage(minioUtil.getChunkInputStream(fileData.getFileMd5(),userId,0)).readAllBytes();
        }
        //生成图片缩略图
        else if (fileType==FileTypeEnum.IMAGE){
            imageBytes=ImgTools.generateSmallerImage(minioUtil.getFileInputStream(fileData.getFileUrl())).toByteArray();
        }
        else
            return;
        String imgName = PathUtil.getPlainName(fileData.getFileName())+".jpg";
        String path = aliOssUtil.upload(imageBytes, imgName);
        fileData.setFileCover(path);
        updateById(fileData);
    }

    @Override
    public FileData getFileDataByUserFileId(Long userFileId,Long userId,Integer status) {
        return fileDataMapper.getFileDataByUserFileId(userFileId,status,userId);
    }
}
