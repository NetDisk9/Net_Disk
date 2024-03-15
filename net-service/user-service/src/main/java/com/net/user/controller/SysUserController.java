package com.net.user.controller;


import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

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
    @GetMapping("/test")
    public ResponseResult login(@RequestHeader Map<String, String> headers){
        System.out.println(headers.get("user-info"));
        return ResponseResult.okResult(ResultCodeEnum.SUCCESS);
    }

}
