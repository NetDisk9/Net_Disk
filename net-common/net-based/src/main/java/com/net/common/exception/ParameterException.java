package com.net.common.exception;

import com.net.common.enums.ResultCodeEnum;

public class ParameterException extends RuntimeException {

    private ResultCodeEnum resultCodeEnum;

    public ParameterException(ResultCodeEnum resultCodeEnum){
        this.resultCodeEnum = resultCodeEnum;
    }
    public ParameterException(){}


    public ResultCodeEnum getResultCodeEnum() {
        return resultCodeEnum;
    }
}