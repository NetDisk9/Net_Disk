package com.net.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.user.entity.LoginLog;
import com.net.user.entity.SysUser;
import com.net.user.mapper.LoginLogMapper;
import com.net.user.mapper.SysUserMapper;
import com.net.user.service.LoginLogService;
import com.net.user.service.SysUserService;
import org.springframework.stereotype.Service;

@Service
public class LoginLogServiceImpl extends ServiceImpl<LoginLogMapper, LoginLog> implements LoginLogService {

}
