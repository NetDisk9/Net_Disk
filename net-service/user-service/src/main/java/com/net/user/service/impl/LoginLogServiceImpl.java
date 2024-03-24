package com.net.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.log.Log;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.user.entity.LoginLog;
import com.net.user.entity.SysUser;
import com.net.user.mapper.LoginLogMapper;
import com.net.user.mapper.SysUserMapper;
import com.net.user.pojo.vo.DeviceVO;
import com.net.user.service.LoginLogService;
import com.net.user.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
    @Override
    public ResponseResult getDevice() {
        LambdaQueryWrapper<LoginLog> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginLog::getUserId, BaseContext.getCurrentId())
                .orderByDesc(LoginLog::getLoginTime)
                .last("limit 1");
        LoginLog one = this.getOne(queryWrapper);
        DeviceVO deviceVO = BeanUtil.copyProperties(one, DeviceVO.class);
        return ResponseResult.okResult(deviceVO);
    }
}
