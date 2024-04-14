package com.net.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.user.entity.SysUserRole;
import com.net.user.mapper.SysUserRoleMapper;
import com.net.user.service.SysUserRoleService;
import org.springframework.stereotype.Service;

@Service
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements SysUserRoleService {
}
