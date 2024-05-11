package com.net.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.common.dto.ResponseResult;
import com.net.user.entity.SysVIPEntity;

public interface SysVIPService extends IService<SysVIPEntity> {
    int isVip(Long userId);
    ResponseResult getVIPinfo(Long userId);
    ResponseResult insertVIPinfo(Long userId, int duration);
    ResponseResult updateVIPDurationTime(Long userId, int duration, boolean updateNowOrEnd);
}
