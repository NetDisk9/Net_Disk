package com.net.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.common.constant.EmailKeyConstants;
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
import com.net.user.entity.SysUserRole;
import com.net.user.entity.SysVIPEntity;
import com.net.user.mapper.SysUserMapper;
import com.net.user.pojo.dto.RegisterDTO;
import com.net.user.pojo.dto.UpdatePasswordDTO;
import com.net.user.pojo.dto.UserDTO;
import com.net.user.pojo.vo.RoleVO;
import com.net.user.pojo.vo.UserInfoVO;
import com.net.user.pojo.vo.VIPVO;
import com.net.user.service.RoleService;
import com.net.user.service.SysUserRoleService;
import com.net.user.service.SysUserService;
import com.net.user.service.SysVIPService;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 用户信息表 服务实现类
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-15
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {
    @Resource
    RedisUtil redisUtil;
    @Resource
    @Lazy
    RoleService roleService;
    @Resource
    SysUserRoleService userRoleService;
    @Resource
    SysVIPService sysVIPService;

    @Override
    public ResponseResult insertRegisterInfo(@RequestBody RegisterDTO registerDTO) {
        System.out.println(registerDTO.getEmail());
        System.out.println(RedisConstants.EMAIL_CODE_REGISTER);
        if (checkUsernameExists(registerDTO.getUsername())) {
            return ResponseResult.errorResult(ResultCodeEnum.USERNAME_HASUSED);
        } else if (checkEmailExists(registerDTO.getEmail())) {
            return ResponseResult.errorResult(ResultCodeEnum.EMAIL_HASUSED);
        } else if (redisUtil.get(RedisConstants.EMAIL_CODE_REGISTER + registerDTO.getEmail()) == null ||
                !Objects.equals(redisUtil.get(RedisConstants.EMAIL_CODE_REGISTER + registerDTO.getEmail()).toString(), registerDTO.getCode())) {
            return ResponseResult.errorResult(ResultCodeEnum.CODE_ERROR);
        }
        SysUser sysUser = SysUser.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(SHAUtil.encrypt(registerDTO.getPassword()))
                .avatar(UserConstants.DEAULT_AVATAR)
                .status(UserConstants.DEAULT_STATUS)
                .method(UserConstants.DEAULT_METHOD) // 默认111，三种方式全开，即ID/用户名/邮箱登录
                .build();
        this.save(sysUser); // 使用MyBatis-Plus的save方法
        SysUserRole sysUserRole = SysUserRole
                .builder()
                .userId(sysUser.getId())
                .roleId(roleService.getRoleVOByName("user").getRoleId())
                .build();
        userRoleService.save(sysUserRole);
        return ResponseResult.okResult(sysUser.getId().toString()); // 插入成功，返回新用户的ID
    }

    @Override
    public boolean checkUsernameExists(String username) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username).select(SysUser::getId);
        SysUser user = this.getOne(queryWrapper);
        return user != null;
    }

    @Override
    public boolean checkEmailExists(String email) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getEmail, email).select(SysUser::getId);
        SysUser user = this.getOne(queryWrapper);
        return user != null;
    }

    @Override
    public ResponseResult forgetPassword(String email, String newPassword) {
        if (!checkEmailExists(email)) return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR, "当前邮箱未注册");
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getEmail, email)
                .set(SysUser::getPassword, SHAUtil.encrypt(newPassword));
        this.update(updateWrapper);
        redisUtil.del(EmailKeyConstants.RESET_PASSWORD_RES_KEY + email);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult checkUserIDExists(Long userID) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getId, userID).select(SysUser::getId);
        SysUser user = this.getOne(queryWrapper);
        if (user == null) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getUserByEmailAndPassword(String email, String password) {
        return getUserByPassword(SysUser::getEmail, email, password);
    }

    public <T> ResponseResult getUserByPassword(SFunction<SysUser, T> supplier, T data, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPassword, SHAUtil.encrypt(password)).eq(supplier, data);
        SysUser user = getOne(queryWrapper);
        if (user == null || user.getId() == null) {
            return ResponseResult.errorResult(ResultCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        return ResponseResult.okResult(user);

    }

    @Override
    public ResponseResult getUserByUserIdAndPassword(Long userId, String password) {
        return getUserByPassword(SysUser::getId, userId, password);

    }

    @Override
    public ResponseResult getUserByUsernameAndPassword(String username, String password) {
        return getUserByPassword(SysUser::getUsername, username, password);

    }

    @Override
    public String getUserLoginCode(String email) {
        Object code = redisUtil.get(EmailKeyConstants.LOGIN_KEY + email);
        return code == null ? null : code.toString();
    }

    @Override
    public ResponseResult updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(SysUser::getId, BaseContext.getCurrentId())
                .set(SysUser::getPassword, SHAUtil.encrypt(updatePasswordDTO.getNewPassword()));
        this.update(updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateUserInfo(UserDTO userDTO) {
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(SysUser::getId, BaseContext.getCurrentId())
                .set(userDTO.getUsername() != null, SysUser::getUsername, userDTO.getUsername())
                .set(userDTO.getNickname() != null, SysUser::getNickname, userDTO.getNickname());
        this.update(updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getUserInfo() {
        System.out.println(BaseContext.getCurrentId());
        // 获取当前用户基本信息
        SysUser user = this.getOne(Wrappers.<SysUser>lambdaQuery().eq(SysUser::getId, BaseContext.getCurrentId()));
        UserInfoVO userInfoVO = BeanUtil.copyProperties(user, UserInfoVO.class);
        userInfoVO.setUserId(BaseContext.getCurrentId());
        // 获取用户最高级角色
        RoleEntity topRole = roleService.getTopRankRoleEntity(BaseContext.getCurrentId());
        userInfoVO.setRoleVO(BeanUtil.copyProperties(topRole, RoleVO.class));
        // 获取当前用户的角色列表
        List<RoleEntity> roleEntities = roleService.listRoleByUserId(BaseContext.getCurrentId());
        long count = roleEntities.stream().filter(roleEntity -> roleEntity.getRoleName().equals(RoleEnum.VIP.getName())).count();
        // 如果是VIP则添加对应的信息
        if (count > 0){
            SysVIPEntity vip = sysVIPService.getOne(Wrappers.<SysVIPEntity>lambdaQuery().eq(SysVIPEntity::getUserId, BaseContext.getCurrentId()));
            userInfoVO.setVipVO(BeanUtil.copyProperties(vip, VIPVO.class));
        }
        return ResponseResult.okResult(userInfoVO);
    }


    @Override
    public void deleteUserLoginCode(String email) {
        redisUtil.del(RedisConstants.EMAIL_CODE_LOGIN + email);
    }

    @Override
    public SysUser getUserByEmail(String email) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getEmail, email);
        SysUser user = getOne(queryWrapper);
        return user;
    }

    @Override
    public ResponseResult getLoginMethod() {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getId, BaseContext.getCurrentId());
        SysUser user = getOne(queryWrapper);
        Map<String, String> data = new HashMap<>();
        data.put("type", user.getMethod());
        return ResponseResult.okResult(data);
    }

    @Override
    public ResponseResult updateLoginMethod(String methods) {
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(SysUser::getId, BaseContext.getCurrentId())
                .set(SysUser::getMethod, methods);
        this.update(updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateAvatar(String avatarPath) {
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(SysUser::getId, BaseContext.getCurrentId())
                .set(SysUser::getAvatar, avatarPath);
        this.update(updateWrapper);
        return ResponseResult.okResult(avatarPath);
    }

    @SneakyThrows
    @Override
    public List<SysUser> addBatchUserByAdmin(int count, long roleId) {
        ArrayList<SysUser> userList = new ArrayList<>();
        ArrayList<SysUserRole> userRoleList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            SysUser sysUser = SysUser.builder()
                    //1768546042247323649
                    .id(Long.valueOf("2" + RandomUtil.randomNumbers(18)))
                    .username(RandomUtil.randomString(6))
                    .password(SHAUtil.encrypt(UserConstants.DEAULT_PASSWORD))
                    .avatar(UserConstants.DEAULT_AVATAR)
                    .status(UserConstants.DEAULT_STATUS)
                    .method(UserConstants.DEAULT_METHOD) // 默认111，三种方式全开，即ID/用户名/邮箱登录
                    .build();
            userList.add(sysUser);

            SysUserRole sysUserRole = SysUserRole
                    .builder()
                    .userId(sysUser.getId())
                    .roleId(roleId)
                    .build();
            userRoleList.add(sysUserRole);
        }
        this.saveBatch(userList);
        userRoleService.saveBatch(userRoleList);
        return userList;
    }

    @Override
    public ResponseResult addUserByAdmin(String username, String password, Long roleId) {
        if (checkUsernameExists(username)) {
            return ResponseResult.errorResult(ResultCodeEnum.USERNAME_HASUSED);
        }
        SysUser sysUser = SysUser.builder()
                .username(username)
                .password(SHAUtil.encrypt(password))
                .avatar(UserConstants.DEAULT_AVATAR)
                .status(UserConstants.DEAULT_STATUS)
                .method(UserConstants.DEAULT_METHOD) // 默认111，三种方式全开，即ID/用户名/邮箱登录
                .build();

        this.save(sysUser);// 先插入角色才能获得Id
        SysUserRole sysUserRole = SysUserRole
                .builder()
                .userId(sysUser.getId())
                .roleId(roleId)
                .build();
        userRoleService.save(sysUserRole);

        return ResponseResult.okResult(sysUser.getId());
    }

    @Override
    public ResponseResult updateVIPDuration(Long userId, int duration, boolean isRenew) {
        if (!isRenew) {
            return sysVIPService.insertVIPinfo(userId, duration);
        } else {
            return sysVIPService.updateVIPDurationTime(userId, duration);
        }
    }
}
