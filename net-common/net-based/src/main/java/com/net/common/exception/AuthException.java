package com.net.common.exception;

import com.net.common.enums.ResultCodeEnum;

public class AuthException extends RuntimeException {

    private ResultCodeEnum resultCodeEnum;

    public AuthException(ResultCodeEnum resultCodeEnum){
        this.resultCodeEnum = resultCodeEnum;
    }
    public AuthException(){}


    public ResultCodeEnum getResultCodeEnum() {
        return resultCodeEnum;
    }
}