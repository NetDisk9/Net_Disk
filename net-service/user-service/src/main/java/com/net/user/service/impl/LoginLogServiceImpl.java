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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {
    @Override
    public ResponseResult getDevice() {
        LambdaQueryWrapper<LoginLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LoginLog::getUserId, BaseContext.getCurrentId());
        List<LoginLog> loginLogs = this.list(queryWrapper);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        Map<String, Integer> uniqueDeviceNames = new HashMap<>();
        List<DeviceVO> devices = new ArrayList<>();
        Integer cnt = 0;
        for (LoginLog log : loginLogs) {
            String deviceName = log.getDeviceName();
            if (uniqueDeviceNames.containsKey(deviceName)) {
                DeviceVO device = devices.get(uniqueDeviceNames.get(deviceName));
                device.setLoginTime(max(LocalDateTime.parse(device.getLoginTime(), formatter), log.getLoginTime()).toString());
                device.setFirstLoginTime(min(LocalDateTime.parse(device.getFirstLoginTime(), formatter), log.getLoginTime()).toString());
            } else {
                uniqueDeviceNames.put(deviceName, cnt);
                devices.add(BeanUtil.copyProperties(log, DeviceVO.class));
                devices.get(cnt).setFirstLoginTime(log.getLoginTime().toString());
                cnt += 1;
            }
        }
        return ResponseResult.okResult(devices);
    }

    @Override
    @Async
    public void saveLog(LoginLog loginLog) {
        save(loginLog);
    }

    public LocalDateTime max(LocalDateTime x, LocalDateTime y) {
        return x.isAfter(y) ? x : y;
    }
    public LocalDateTime min(LocalDateTime x, LocalDateTime y) {
        return x.isAfter(y) ? y : x;
    }
}
