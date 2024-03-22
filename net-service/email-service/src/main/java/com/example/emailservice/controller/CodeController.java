package com.example.emailservice.controller;

import com.example.emailservice.service.EmailService;
import com.net.common.dto.ResponseResult;
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
        emailService.sendCode(email,type);
        return ResponseResult.okResult(null);
    }
}
