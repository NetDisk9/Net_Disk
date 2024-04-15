package com.net.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.net.user.entity.RoleEntity;
import com.net.user.entity.RoleSimpleEntity;

import java.util.List;

public interface RoleMapper extends BaseMapper<RoleEntity> {
    public List<RoleEntity> listRoleByUserId(Long userId);
    public void updateRoleByUserIdAndUserRank(Long userId, Integer userRoleRank, Integer updateRoleRank);
    public void deleteRoleByUserIdAndUSerRank(Long userId, Integer updateRoleRank, Integer userRoleRank);
    public void updateUserPassword(String defultPassword, Long userId);
    public List<RoleSimpleEntity> listSimpleRoleByBaseContextRoleRank(Integer baseContextRoleRank);
}
