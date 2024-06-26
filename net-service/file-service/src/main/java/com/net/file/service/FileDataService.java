package com.net.file.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.file.entity.FileData;

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
    FileData getFileDataByUserFileId(Long userFileId,Long userId,Integer status);
    void generateImageCover(FileData fileData,Long userId)throws Exception;

}
