package com.net.user.controller;


import cn.hutool.core.bean.BeanUtil;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.alibaba.spring.util.BeanUtils;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.user.entity.SysUser;
import com.net.user.pojo.dto.LoginDTO;
import com.net.user.pojo.dto.RegisterDTO;
import com.net.user.pojo.dto.UpdatePasswordDTO;
import com.net.user.pojo.dto.UserDTO;
import com.net.user.pojo.vo.UserVO;
import com.net.user.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.jcajce.provider.digest.SHA256;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


/**
 * <p>
 * 用户信息表 前端控制器
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-15
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {
    private final SysUserService userService;

    @PostMapping("/login")
    public ResponseResult login(@RequestBody LoginDTO loginDTO){
        return ResponseResult.okResult(ResultCodeEnum.SUCCESS);
    }
    @PostMapping("/register")
    public ResponseResult register(@RequestBody RegisterDTO registerDTO){
        return ResponseResult.okResult(ResultCodeEnum.SUCCESS);
    }
    @GetMapping("/info")
    public ResponseResult getUserInfo(){
        LambdaQueryWrapper<SysUser> queryWrapper =new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getId,1);
        SysUser one = userService.getOne(queryWrapper);
        UserVO userVO = BeanUtil.copyProperties(one, UserVO.class);
        return ResponseResult.okResult(userVO);
    }
    @PutMapping("/update")
    public ResponseResult update(@RequestBody UserDTO userDTO){
        LambdaUpdateWrapper<SysUser>updateWrapper =new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getId, 1).
                set(SysUser::getUsername, userDTO.getUsername());
        userService.update(updateWrapper);
        return ResponseResult.okResult(ResultCodeEnum.SUCCESS);
    }
    @PutMapping("/update/password")
    public ResponseResult updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO){
        if (!updatePasswordDTO.getOldPassword().equals(updatePasswordDTO.getNewPassword())) {
            return ResponseResult.errorResult(ResultCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        return ResponseResult.okResult(ResultCodeEnum.SUCCESS);
    }
    @PostMapping("/avatar")
    public ResponseResult uploadAvatar(@RequestBody MultipartFile multipartFile){
        return ResponseResult.okResult(ResultCodeEnum.SUCCESS);
    }

}
