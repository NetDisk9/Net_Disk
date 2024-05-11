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
    public int isVip(Long userId) {
        LambdaQueryWrapper<SysVIPEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysVIPEntity::getUserId, userId);
        if (this.count(queryWrapper)==0) {
            return 0;
        } else {
            SysVIPEntity sysVIPEntity = this.getOne(queryWrapper);
            if (!DateFormatUtil.isBefore(sysVIPEntity.getEndTime(), DateFormatUtil.getNow())) {
                return 1;
            } else {
                return 2;
            }
        }
    }

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
    public ResponseResult updateVIPDurationTime(Long userId, int duration, boolean updateNowOrEnd) {
        SysVIPEntity currentVIPEntity = (SysVIPEntity) getVIPinfo(userId).getData();
        LambdaUpdateWrapper<SysVIPEntity>updateWrapper = new LambdaUpdateWrapper<>();
        String updatedTime;
        String NowTime = DateFormatUtil.getNow();
        if (updateNowOrEnd) {
            updatedTime = DateFormatUtil.addDays(NowTime, duration);
            updateWrapper.eq(SysVIPEntity::getUserId, userId)
                .set(SysVIPEntity::getEndTime, DateFormatUtil.addDays(NowTime, duration));
            this.update(updateWrapper);
        } else {
            updatedTime = DateFormatUtil.addDays(currentVIPEntity.getEndTime(), duration);
            updateWrapper.eq(SysVIPEntity::getUserId, userId)
                    .set(SysVIPEntity::getEndTime, updatedTime);
            this.update(updateWrapper);
        }
        updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysVIPEntity::getUserId, userId)
                .set(SysVIPEntity::getUpdateTime, NowTime);
        this.update(updateWrapper);
        return ResponseResult.okResult(updatedTime);
    }
}
