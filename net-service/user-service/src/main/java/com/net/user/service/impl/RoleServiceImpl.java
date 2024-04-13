package com.net.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.net.common.context.BaseContext;
import com.net.common.enums.RoleEnum;
import com.net.redis.constant.RedisConstants;
import com.net.redis.utils.RedisUtil;
import com.net.user.entity.RoleEntity;
import com.net.user.mapper.RoleMapper;
import com.net.user.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements RoleService {
    @Resource
    RedisUtil redisUtil;
    @Resource
    RoleMapper roleMapper;
    ObjectMapper objectMapper=new ObjectMapper();
    public List<RoleEntity> listRoleByUserId() {
        Long userId=BaseContext.getCurrentId();
        try {
            if (redisUtil.hasKey(RedisConstants.USER_ROLE + userId)) {
                List<RoleEntity> list = objectMapper.readValue((String) redisUtil.get(RedisConstants.USER_ROLE + userId), new TypeReference<List<RoleEntity>>() {});
                return list;
            }
        }
        catch (Exception e){
            e.printStackTrace();;
        }
        List<RoleEntity> list=roleMapper.listRoleByUserId(userId);
        try {
            redisUtil.set(RedisConstants.USER_ROLE+userId,objectMapper.writeValueAsString(list), RedisConstants.LOGIN_USER_TTL);
        }catch (Exception e){
            throw new RuntimeException();
        }
        return list;
    }

    @Override
    public Boolean isSuperAdministrator() {
        List<RoleEntity> list=listRoleByUserId();
        System.out.println(list);
        String superName= RoleEnum.SUPER.getName();
        return list.stream().map(RoleEntity::getRoleName).filter(name -> {
            return superName.equals(name);
        }).count()==1;
    }
    @Override
    public Boolean isAdministrator() {
        if(isSuperAdministrator()){
            return false;
        }
        List<RoleEntity> list=listRoleByUserId();
        String superName= RoleEnum.ADMIN.getName();
        return list.stream().map(RoleEntity::getRoleName).filter(name -> {
            return superName.equals(name);
        }).count()==1;
    }

    @Override
    public RoleEntity getRoleVOByName(String name) {
        return getOne(new LambdaQueryWrapper<RoleEntity>().eq(RoleEntity::getRoleName,name));
    }
}
