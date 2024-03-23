package com.net.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.util.SHAUtil;
import com.net.redis.utils.RedisUtil;
import com.net.user.entity.SysUser;
import com.net.user.mapper.SysUserMapper;
import com.net.user.pojo.dto.UpdatePasswordDTO;
import com.net.user.pojo.dto.UserDTO;
import com.net.user.pojo.vo.UserVO;
import com.net.user.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.net.user.util.RegexUtil;
import io.netty.util.internal.StringUtil;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.List;
import java.util.function.Supplier;

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
    public ResponseResult getUserIdByEmailAndPassword(String email, String password) {
        return getUserIdByPassword(SysUser::getEmail,email,password);
    }
    public<T> ResponseResult getUserIdByPassword(SFunction<SysUser,T> supplier, T data, String password){
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getPassword,SHAUtil.encrypt(password)).eq(supplier,data).select(SysUser::getId);
        SysUser user=getOne(queryWrapper);
        if(user==null||user.getId()==null){
            return ResponseResult.errorResult(403,"用户名或密码错误");
        }
        return ResponseResult.okResult(user.getId());

    }

    @Override
    public ResponseResult getUserIdByUserIdAndPassword(Long userId, String password) {
        return getUserIdByPassword(SysUser::getId,userId,password);

    }

    @Override
    public ResponseResult getUserIdByUsernameAndPassword(String username, String password) {
        return getUserIdByPassword(SysUser::getUsername,username,password);

    }

    @Override
    public String getUserLoginCode(String email) {
        String code=redisUtil.get("email:code:login:"+email).toString();
        return code;
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
        redisUtil.del("email:code:login:"+email);
    }

    @Override
    public Long getUserIdByEmail(String email) {
        LambdaQueryWrapper<SysUser> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getEmail,email).select(SysUser::getId);
        SysUser user=getOne(queryWrapper);
        return user.getId();
    }
}
