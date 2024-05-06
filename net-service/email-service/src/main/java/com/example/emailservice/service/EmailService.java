package com.example.emailservice.service;

import com.net.common.dto.ResponseResult;
import com.net.common.exception.ParameterException;
import org.springframework.stereotype.Service;

public interface EmailService {
    ResponseResult sendCode(String email, String type);
    boolean checkCode(String email,String code,String type);
    void deleteCode(String email,String type);
    void saveRes(String email,String code,String type) throws ParameterException;
}
