package com.net.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.net.common.util.SHAUtil;
import com.net.redis.constant.RedisConstants;
import com.net.redis.utils.RedisUtil;
import com.net.user.entity.SysUser;
import com.net.user.mapper.SysUserMapper;
import com.net.user.pojo.dto.RegisterDTO;
import com.net.user.pojo.dto.UpdatePasswordDTO;
import com.net.user.pojo.dto.UserDTO;
import com.net.user.pojo.vo.UserVO;
import com.net.user.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    @Autowired
    RedisUtil redisUtil;
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
                .avatar("https://wangluodapangzi.oss-cn-shenzhen.aliyuncs.com/595d009f-bffc-4efd-b7cf-24262a323ceb.png")
                .status(0)
                .method("111") // 默认111，三种方式全开，即ID/用户名/邮箱登录
                .build();
        this.save(sysUser); // 使用MyBatis-Plus的save方法
        return ResponseResult.okResult(sysUser.getId().toString()); // 插入成功，返回新用户的ID
    }
    public boolean checkUsernameExists(String username) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getUsername, username).select(SysUser::getId);
        SysUser user = this.getOne(queryWrapper);
        return user != null;
    }
    public boolean checkEmailExists(String email) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getEmail, email).select(SysUser::getId);
        SysUser user = this.getOne(queryWrapper);
        return user != null;
    }

    @Override
    public ResponseResult getUserByEmailAndPassword(String email, String password) {
        return getUserByPassword(SysUser::getEmail,email,password);
    }
    public<T> ResponseResult getUserByPassword(SFunction<SysUser,T> supplier, T data, String password){
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPassword,SHAUtil.encrypt(password)).eq(supplier,data);
        SysUser user=getOne(queryWrapper);
        if(user==null||user.getId()==null){
            return ResponseResult.errorResult(ResultCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        return ResponseResult.okResult(user);

    }

    @Override
    public ResponseResult getUserByUserIdAndPassword(Long userId, String password) {
        return getUserByPassword(SysUser::getId,userId,password);

    }

    @Override
    public ResponseResult getUserByUsernameAndPassword(String username, String password) {
        return getUserByPassword(SysUser::getUsername,username,password);

    }

    @Override
    public String getUserLoginCode(String email) {
        Object code=redisUtil.get(RedisConstants.EMAIL_CODE_LOGIN+email);
        return code==null?null:code.toString();
    }

    @Override
    public ResponseResult updatePassword(UpdatePasswordDTO updatePasswordDTO) {
        LambdaUpdateWrapper<SysUser> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(SysUser::getId, BaseContext.getCurrentId())
                .set(SysUser::getPassword, SHAUtil.encrypt(updatePasswordDTO.getNewPassword()));
        this.update(updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateUserInfo(UserDTO userDTO) {
        LambdaUpdateWrapper<SysUser>updateWrapper =new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(SysUser::getId, BaseContext.getCurrentId())
                .set(userDTO.getUsername()!=null,SysUser::getUsername, userDTO.getUsername())
                .set(userDTO.getNickname()!=null,SysUser::getNickname, userDTO.getNickname());
        this.update(updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult getUserInfo() {
        LambdaQueryWrapper<SysUser> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getId,BaseContext.getCurrentId());
        SysUser one = this.getOne(queryWrapper);
        UserVO userVO = BeanUtil.copyProperties(one, UserVO.class);
        return ResponseResult.okResult(userVO);
    }


    @Override
    public void deleteUserLoginCode(String email) {
        redisUtil.del(RedisConstants.EMAIL_CODE_LOGIN+email);
    }

    @Override
    public SysUser getUserByEmail(String email) {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getEmail,email);
        SysUser user=getOne(queryWrapper);
        return user;
    }

    @Override
    public ResponseResult getLoginMethod() {
        LambdaQueryWrapper<SysUser> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getId,BaseContext.getCurrentId());
        SysUser user = getOne(queryWrapper);
        Map<String, String> data = new HashMap<>();
        data.put("type", user.getMethod());
        return ResponseResult.okResult(data);
    }

    @Override
    public ResponseResult updateLoginMethod(String methods) {
        LambdaUpdateWrapper<SysUser> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(SysUser::getId, BaseContext.getCurrentId())
                .set(SysUser::getMethod, methods);
        this.update(updateWrapper);
        return ResponseResult.okResult();
    }

    @Override
    public ResponseResult updateAvatar(String avatarPath) {
        LambdaUpdateWrapper<SysUser> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(SysUser::getId, BaseContext.getCurrentId())
                .set(SysUser::getAvatar, avatarPath);
        this.update(updateWrapper);
        return ResponseResult.okResult(avatarPath);
    }
}
