package com.net.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.user.entity.RoleEntity;

public interface RoleService extends IService<RoleEntity> {
    public Boolean isSuperAdministrator();
    public Boolean isAdministrator();
    public RoleEntity getRoleVOByName(String name);
}
