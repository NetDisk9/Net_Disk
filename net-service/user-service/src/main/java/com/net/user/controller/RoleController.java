package com.net.user.controller;

import com.net.common.context.BaseContext;
import com.net.user.service.RoleService;
import com.net.user.service.SysVIPService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    RoleService roleService;
    @Resource
    SysVIPService sysVIPService;
    @GetMapping("/issuper")
    public String isSuperAdministrator(){
        Long userId= BaseContext.getCurrentId();
        return roleService.isSuperAdministrator(userId).toString();
    }
    @GetMapping("/isvip")
    public String isVIP(){
        Long userId= BaseContext.getCurrentId();
        return String.valueOf(sysVIPService.isVip(userId));
    }

}
