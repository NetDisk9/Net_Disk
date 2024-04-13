package com.net.user.controller;

import com.net.common.context.BaseContext;
import com.net.user.entity.RoleEntity;
import com.net.user.service.RoleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/role")
public class RoleController {
    @Resource
    RoleService roleService;
    @GetMapping("/issuper")
    public String isSuperAdministrator(@RequestParam String id){
        Long userId= BaseContext.getCurrentId();
        return roleService.isSuperAdministrator(userId).toString();
    }

}
