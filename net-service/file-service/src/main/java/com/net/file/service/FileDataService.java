package com.net.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.common.enums.FileTypeEnum;
import com.net.file.entity.FileData;

import java.io.InputStream;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-05-11
 */
public interface FileDataService extends IService<FileData> {
    FileData getFileDataByMd5(String md5);
    void generateImageCover(FileData fileData,Long userId)throws Exception;

}
