package com.example.emailservice.service;

import com.net.common.dto.ResponseResult;
import org.springframework.stereotype.Service;

public interface EmailService {
    public ResponseResult sendCode(String email, String type);
    public boolean checkCode(String email,String code,String type);
}
