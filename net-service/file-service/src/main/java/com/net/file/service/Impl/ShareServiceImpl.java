package com.net.file.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.exception.ParameterException;
import com.net.common.util.DateFormatUtil;
import com.net.file.constant.ShareStatusConstants;
import com.net.file.entity.ShareEntity;
import com.net.file.mapper.ShareMapper;
import com.net.file.pojo.dto.FileShareDTO;
import com.net.file.pojo.vo.FileVO;
import com.net.file.pojo.vo.ShareInfoVO;
import com.net.file.service.ShareService;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, ShareEntity> implements ShareService {
    @Resource
    ShareMapper shareMapper;

    @Override
    public ShareEntity getShareEntityWithCheck(String link) {
        LambdaQueryWrapper<ShareEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShareEntity::getLink, link);
        ShareEntity shareEntity = getOne(queryWrapper);
        if (checkShareValid(shareEntity)) {
            throw new ParameterException("分享链接失效");
        }
        return shareEntity;
    }

    @Override
    public boolean checkShareValid(ShareEntity shareEntity) {
        if (!ShareStatusConstants.VALID.equals(shareEntity.getStatus())) {
            return false;
        }
        LocalDateTime endDateTime = DateFormatUtil.string2LocalDateTime(shareEntity.getEndTime());
        LocalDateTime now = LocalDateTime.now();
        if (!now.isBefore(endDateTime)) {
            return false;
        }
        return true;
    }

    @Override
    public ShareInfoVO getShareInfoByLink(String link) {
        return shareMapper.getShareInfoByLink(link);
    }

    @Override
    public IPage listShareFile(Page<FileVO> pageInfo, @Param("param") FileShareDTO fileShareDTO) {
        return shareMapper.listShareFile(pageInfo, fileShareDTO);
    }
}
