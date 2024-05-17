package com.net.file.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.file.entity.FileData;
import com.net.file.mapper.FileDataMapper;
import com.net.file.service.FileDataService;
import org.springframework.stereotype.Service;

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
    @Override
    public FileData getFileDataByMd5(String md5) {
        LambdaQueryWrapper<FileData> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(FileData::getFileMd5,md5);
        return getOne(queryWrapper);
    }
}
