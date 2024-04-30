package com.net.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.dto.ResponseResult;
import com.net.common.util.DateFormatUtil;
import com.net.user.entity.SysVIPEntity;
import com.net.user.mapper.SysVIPMapper;
import com.net.user.service.SysVIPService;
import org.springframework.stereotype.Service;

@Service
public class SysVIPServiceImpl extends ServiceImpl<SysVIPMapper, SysVIPEntity> implements SysVIPService {
    @Override
    public ResponseResult getVIPinfo(Long userId) {
        LambdaQueryWrapper<SysVIPEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysVIPEntity::getUserId, userId);
        SysVIPEntity sysVIPEntity = this.getOne(queryWrapper);
        return ResponseResult.okResult(sysVIPEntity);
    }

    @Override
    public ResponseResult insertVIPinfo(Long userId, int duration) {
        String endTime = DateFormatUtil.addDays(DateFormatUtil.getNow(), duration);
        SysVIPEntity sysVIPEntity = SysVIPEntity.builder()
                .userId(userId)
                .updateTime(DateFormatUtil.getNow())
                .endTime(endTime)
                .build();
        this.save(sysVIPEntity);
        return ResponseResult.okResult(endTime);
    }

    @Override
    public ResponseResult updateVIPDurationTime(Long userId, int duration) {
        SysVIPEntity currentVIPEntity = (SysVIPEntity) getVIPinfo(userId).getData();
        String updatedTime = DateFormatUtil.addDays(currentVIPEntity.getEndTime(), duration);
        LambdaUpdateWrapper<SysVIPEntity>updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysVIPEntity::getUserId, userId)
                .set(SysVIPEntity::getEndTime, updatedTime);
        this.update(updateWrapper);
        return ResponseResult.okResult(updatedTime);
    }
}
