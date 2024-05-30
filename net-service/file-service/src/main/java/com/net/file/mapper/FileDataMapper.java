package com.net.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.net.file.entity.FileData;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-05-11
 */
public interface FileDataMapper extends BaseMapper<FileData> {
    FileData getFileDataByUserFileId(Long userFileId,Integer status,Long userId);

}
