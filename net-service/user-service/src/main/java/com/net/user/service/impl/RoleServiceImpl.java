package com.net.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.enums.RoleEnum;
import com.net.common.util.SHAUtil;
import com.net.redis.constant.RedisConstants;
import com.net.redis.utils.RedisUtil;
import com.net.user.constant.UserConstants;
import com.net.user.entity.RoleEntity;
import com.net.user.entity.SysUser;
import com.net.user.mapper.RoleMapper;
import com.net.user.pojo.vo.UserInfoVO;
import com.net.user.pojo.vo.UserVO;
import com.net.user.service.RoleService;
import com.net.user.service.SysUserRoleService;
import com.net.user.service.SysUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, RoleEntity> implements RoleService {
    @Resource
    RedisUtil redisUtil;
    @Resource
    RoleMapper roleMapper;
    @Resource
    SysUserService sysUserService;
    ObjectMapper objectMapper=new ObjectMapper();

    @Override
    public List<RoleEntity> listRoleByUserId(Long userId) {
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
    public Boolean isSuperAdministrator(Long userId) {
        List<RoleEntity> list=listRoleByUserId(userId);
        System.out.println(list);
        String superName= RoleEnum.SUPER.getName();
        return list.stream().map(RoleEntity::getRoleName).anyMatch(superName::equals);
    }
    @Override
    public Boolean isAdministrator(Long userId) {
        if(isSuperAdministrator(userId)){
            return false;
        }
        List<RoleEntity> list=listRoleByUserId(userId);
        String adminName= RoleEnum.ADMIN.getName();
        return list.stream().map(RoleEntity::getRoleName).anyMatch(adminName::equals);
    }

    @Override
    public Boolean isVIP(Long userId) {
        List<RoleEntity> list=listRoleByUserId(userId);
        String superName= RoleEnum.VIP.getName();
        return list.stream().map(RoleEntity::getRoleName).filter(name -> {
            return superName.equals(name);
        }).count()==1;
    }

    @Override
    public RoleEntity getTopRankRoleEntity(Long userId) {
        List<RoleEntity> list=listRoleByUserId(userId);
        return list.stream().max(RoleEntity.RoleEntityComparator.getInstance()).get();
    }

    @Override
    public RoleEntity getRoleVOByName(String name) {
        return getOne(new LambdaQueryWrapper<RoleEntity>().eq(RoleEntity::getRoleName,name));
    }

    @Override
    public RoleEntity getRoleVOByRoleId(Long roleId) {
        return getOne(new LambdaQueryWrapper<RoleEntity>().eq(RoleEntity::getRoleId,roleId));
    }

    @Override
    public ResponseResult checkAuthority(Long userId, Long roleId) {
        ResponseResult result = sysUserService.getUserInfo();
        if (result.getCode() != 200) {
            return result;
        }
        Long baseContextUserId = ((UserInfoVO) result.getData()).getUserId();
        int baseContextRoleRank = getTopRankRoleEntity(baseContextUserId).getRoleRank();
        int userRoleRank = getTopRankRoleEntity(userId).getRoleRank();
        int updateRoleRank = getRoleVOByRoleId(roleId).getRoleRank();
        if (baseContextRoleRank <= userRoleRank || updateRoleRank >= baseContextRoleRank) {
            return ResponseResult.errorResult(ResultCodeEnum.UNAUTHORIZED);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult checkAuthorityForPassword(Long userId) {
        ResponseResult result = sysUserService.getUserInfo();
        if (result.getCode() != 200) {
            return result;
        }
        Long baseContextUserId = ((UserInfoVO) result.getData()).getUserId();
        int baseContextRoleRank = getTopRankRoleEntity(baseContextUserId).getRoleRank();
        int userRoleRank = getTopRankRoleEntity(userId).getRoleRank();
        if (baseContextRoleRank <= userRoleRank) {
            return ResponseResult.errorResult(ResultCodeEnum.UNAUTHORIZED);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateUserRole(Long userId, Long roleId) {
        int userRoleRank = getTopRankRoleEntity(userId).getRoleRank();
        int updateRoleRank = getRoleVOByRoleId(roleId).getRoleRank();
        if (updateRoleRank > userRoleRank) {
            roleMapper.updateRoleByUserIdAndUserRank(userId, userRoleRank, updateRoleRank);
        } else if (updateRoleRank < userRoleRank){
            roleMapper.deleteRoleByUserIdAndUSerRank(userId, updateRoleRank, userRoleRank);
        }
        redisUtil.del(RedisConstants.USER_ROLE+userId);
        redisUtil.del(RedisConstants.USER_PERMISSION+userId);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateUserPassword(Long userId) {
        roleMapper.updateUserPassword(SHAUtil.encrypt(UserConstants.DEAULT_PASSWORD), userId);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getModifiableUserRole() {
        ResponseResult result = sysUserService.getUserInfo();
        if (result.getCode() != 200) {
            return result;
        }
        Long baseContextUserId = ((UserInfoVO) result.getData()).getUserId();
        int baseContextRoleRank = getTopRankRoleEntity(baseContextUserId).getRoleRank();
        return ResponseResult.okResult(roleMapper.listSimpleRoleByBaseContextRoleRank(baseContextRoleRank));
    }
}
