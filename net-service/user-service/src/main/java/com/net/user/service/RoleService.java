package com.net.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.user.entity.RoleEntity;

public interface RoleService extends IService<RoleEntity> {
    public Boolean isSuperAdministrator(Long userId);
    public Boolean isAdministrator(Long userId);
    public RoleEntity getRoleVOByName(String name);
    public RoleEntity getTopRankRoleEntity(Long userId);
}
