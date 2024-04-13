package com.net.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.net.user.entity.PermissionEntity;

import java.util.List;

public interface PermissionMapper extends BaseMapper<PermissionEntity> {
    public List<String> listPath(Long userId);
}
