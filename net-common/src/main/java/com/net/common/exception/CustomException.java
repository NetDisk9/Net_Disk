package com.net.common.exception;


import com.net.common.enums.ResultCodeEnum;

public class CustomException extends RuntimeException {

    private ResultCodeEnum resultCodeEnum;

    public CustomException(ResultCodeEnum resultCodeEnum){
        this.resultCodeEnum = resultCodeEnum;
    }

    public ResultCodeEnum getResultCodeEnum() {
        return resultCodeEnum;
    }
}
