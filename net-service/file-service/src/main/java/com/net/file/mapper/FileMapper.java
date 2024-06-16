package com.net.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.net.file.entity.UserFileEntity;
import com.net.file.pojo.dto.FileCollectQueryDTO;
import com.net.file.pojo.dto.FileQueryDTO;
import com.net.file.pojo.vo.FileSendPageVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileMapper extends BaseMapper<UserFileEntity> {
    List<UserFileEntity> listUserFileByPidAndPath(Long pid,String path,Integer status,Long userId);
    UserFileEntity getUserFileByUserFileId(Long id);
    UserFileEntity getUserFileByPath(Long userId,Integer status,String path);
    IPage selectPageVO(Page<UserFileEntity> pageInfo, @Param("param") FileQueryDTO fileQueryDTO);
    IPage<FileSendPageVO> selectSendPageVO(Page<FileSendPageVO> pageInfo, @Param("param") FileCollectQueryDTO fileQueryDTO);
}
