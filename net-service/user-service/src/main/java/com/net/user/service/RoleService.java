package com.net.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.net.common.dto.ResponseResult;
import com.net.user.entity.RoleEntity;

public interface RoleService extends IService<RoleEntity> {
    public Boolean isSuperAdministrator(Long userId);
    public Boolean isAdministrator(Long userId);
    public Boolean isVIP(Long userId);
    public RoleEntity getRoleVOByName(String name);
    public RoleEntity getRoleVOByRoleId(Long roleId);
    public RoleEntity getTopRankRoleEntity(Long userId);
    public ResponseResult checkAuthority(Long userId, Long roleId);
    public ResponseResult checkAuthorityForPassword(Long userId);
    public ResponseResult updateUserRole(Long userId, Long roleId);
    public ResponseResult updateUserPassword(Long userId);
    public ResponseResult getModifiableUserRole();
}
