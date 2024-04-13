package com.net.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.net.user.entity.RoleEntity;

import java.util.List;

public interface RoleMapper extends BaseMapper<RoleEntity> {
    public List<RoleEntity> listRoleByUserId(Long userId);
}
