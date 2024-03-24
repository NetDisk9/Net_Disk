package com.net.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.common.dto.ResponseResult;
import com.net.user.entity.LoginLog;
import com.net.user.entity.SysUser;

public interface LoginLogService extends IService<LoginLog> {
    ResponseResult getDevice();
}
