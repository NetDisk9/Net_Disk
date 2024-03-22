package com.example.emailservice.service.impl;

import com.example.emailservice.service.EmailService;
import com.net.common.dto.ResponseResult;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service

public class EmailServiceImpl implements EmailService {
    @Autowired
    JavaMailSender javaMailSender;
    @Value("key")
    Map<String,String> keyMap;
    @Override
    public ResponseResult sendCode(String email, String type) {
        if(keyMap.containsKey(email)){
            return null;
        }
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom("872919781@qq.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("title");
        int code=RandomUtils.nextInt((int)1e5,(int)1e6-1);
        simpleMailMessage.setText(code+"");
        javaMailSender.send(simpleMailMessage);
        return null;
    }
}
