package com.net.user.controller;


import com.net.common.context.BaseContext;
import com.net.user.service.PermissionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/permission")
@RestController
public class PermissionController {
    @Resource
    PermissionService permissionService;
    @GetMapping("/authenticate")
    public String havePermission(@RequestParam String path){
        Long userId=BaseContext.getCurrentId();
        System.out.println("test");
        return permissionService.havePermission(path).toString();
    }

}
