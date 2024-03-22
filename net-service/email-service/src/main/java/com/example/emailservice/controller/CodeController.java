package com.example.emailservice.controller;

import com.example.emailservice.service.EmailService;
import com.net.common.dto.ResponseResult;
import com.net.common.util.MatchUtil;
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
    private static final String EMAIL_SUFFIX="^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(.[a-zA-Z0-9_-]+)+$";
    @GetMapping("/code")
    public ResponseResult sendCode(String email,String type){
        if(!new MatchUtil(EMAIL_SUFFIX).match(email)){
            return ResponseResult.errorResult(422,"参数错误");
        }
        if(StringUtil.isNullOrEmpty(email)||StringUtil.isNullOrEmpty(type)){
            return ResponseResult.errorResult(422,"参数错误");
        }
        return emailService.sendCode(email,type);
    }
}
