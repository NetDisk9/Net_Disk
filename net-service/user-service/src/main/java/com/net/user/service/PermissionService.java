package com.net.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.user.entity.PermissionEntity;

import java.util.List;

public interface PermissionService extends IService<PermissionEntity> {
    public Boolean havePermission(String path);
    public List<String> getPermissionList(Long userId);
}
