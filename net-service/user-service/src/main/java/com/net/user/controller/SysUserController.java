package com.net.user.controller;


import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.user.pojo.dto.LoginDTO;
import com.net.user.pojo.dto.RegisterDTO;
import com.net.user.pojo.dto.UpdatePasswordDTO;
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
public class SysUserController {
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

}
