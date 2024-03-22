package com.example.emailservice.controller;

import com.example.emailservice.service.EmailService;
import com.net.common.dto.ResponseResult;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
;

@RequestMapping("/email")
@RestController
public class CodeController {
    @Autowired
    EmailService emailService;
    @GetMapping("/code")
    public ResponseResult sendCode(String email,String type){
        if(StringUtil.isNullOrEmpty(email)||StringUtil.isNullOrEmpty(type)){
            return ResponseResult.errorResult(422,"参数错误");
        }
        return emailService.sendCode(email,type);
    }
}
