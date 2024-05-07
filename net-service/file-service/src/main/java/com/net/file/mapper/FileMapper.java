package com.net.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.net.file.entity.UserFileEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileMapper extends BaseMapper<UserFileEntity> {
    List<UserFileEntity> listUserFileByPidAndPath(Long pid,String path,Integer status,Long userId);
    UserFileEntity getUserFileByUserFileId(Long id);
}
