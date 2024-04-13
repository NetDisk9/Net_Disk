package com.net.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.context.BaseContext;
import com.net.redis.constant.RedisConstants;
import com.net.redis.utils.RedisUtil;
import com.net.user.entity.PermissionEntity;
import com.net.user.mapper.PermissionMapper;
import com.net.user.service.PermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, PermissionEntity> implements PermissionService {
    @Resource
    PermissionMapper permissionMapper;
    @Resource
    RedisUtil redisUtil;
    @Override
    public Boolean havePermission(String path) {
        Long userId= BaseContext.getCurrentId();
        Set set=redisUtil.sGet(RedisConstants.USER_PERMISSION+userId);
        if(set!=null&&!set.isEmpty()){
            return set.contains(path);
        }
        List<String> permissionList = getPermissionList(userId);
        redisUtil.sSetAndTime(RedisConstants.USER_PERMISSION+userId,RedisConstants.LOGIN_USER_TTL,permissionList.toArray());
        return permissionList.contains(path);
    }

    @Override
    public List<String> getPermissionList(Long userId) {
        return permissionMapper.listPath(userId);
    }
}
