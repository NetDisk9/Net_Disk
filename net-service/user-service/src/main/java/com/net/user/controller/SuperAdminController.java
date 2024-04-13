package com.net.user.controller;

import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.util.DateFormatUtil;
import com.net.user.pojo.dto.UserQueryDTO;
import com.net.user.service.AdminService;
import com.net.user.service.SuperAdminService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/super")
public class SuperAdminController extends BaseAdminController {
    @Resource
    SuperAdminService superAdminService;
}
