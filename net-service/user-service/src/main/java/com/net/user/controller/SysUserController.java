package com.net.user.controller;


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.user.entity.SysUser;
import com.net.user.pojo.dto.LoginDTO;
import com.net.user.pojo.dto.RegisterDTO;
import com.net.user.pojo.dto.UpdatePasswordDTO;
import com.net.user.pojo.dto.UserDTO;
import com.net.user.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


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
    @PutMapping("/update/password")
    public ResponseResult updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO){
        return ResponseResult.okResult(ResultCodeEnum.SUCCESS);
    }
    @PutMapping("/update")
    public ResponseResult update(@RequestBody UserDTO userDTO){
        LambdaUpdateWrapper<SysUser> wrapper = new LambdaUpdateWrapper<>();
//        wrapper.eq(SysUser::getId, BaseContext.getCurrentId());
        wrapper.eq(SysUser::getId, 1);
        wrapper.set(SysUser::getUsername, userDTO.getUsername());
        userService.update(wrapper);
        return ResponseResult.okResult(ResultCodeEnum.SUCCESS);
    }

}
