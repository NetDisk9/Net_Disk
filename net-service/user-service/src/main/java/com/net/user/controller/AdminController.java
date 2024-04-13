package com.net.user.controller;

import com.alibaba.nacos.api.common.ResponseCode;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.util.DateFormatUtil;
import com.net.user.pojo.dto.UserQueryDTO;
import com.net.user.service.AdminService;
import io.netty.util.internal.StringUtil;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RequestMapping("/admin")
@RestController
public class AdminController extends BaseAdminController{
    @Resource
    AdminService adminService;
    @PostMapping("/list")
    public ResponseResult listUser(@RequestBody UserQueryDTO userQueryDTO,int page,int pageSize){
        if(!initQuery(userQueryDTO,page,pageSize)){
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        return ResponseResult.okResult(adminService.listUser(userQueryDTO));
    }

}
