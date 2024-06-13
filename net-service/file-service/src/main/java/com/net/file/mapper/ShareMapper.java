package com.net.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.net.file.entity.ShareEntity;
import com.net.file.pojo.dto.FileShareDTO;
import com.net.file.pojo.vo.FileVO;
import com.net.file.pojo.vo.ShareInfoVO;
import org.apache.ibatis.annotations.Param;

public interface ShareMapper extends BaseMapper<ShareEntity> {

    ShareInfoVO getShareInfoByLink(String link);

    IPage<FileVO>listShareFile(Page<FileVO> pageInfo, @Param("param") FileShareDTO fileShareDTO);
}


