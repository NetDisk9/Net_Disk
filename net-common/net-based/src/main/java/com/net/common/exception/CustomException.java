package com.net.common.exception;


import com.net.common.enums.ResultCodeEnum;

/**
 * <p>
 * 通用异常
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-21
 */
public class CustomException extends RuntimeException {

    private ResultCodeEnum resultCodeEnum;

    public CustomException(ResultCodeEnum resultCodeEnum){
        this.resultCodeEnum = resultCodeEnum;
    }

    public ResultCodeEnum getResultCodeEnum() {
        return resultCodeEnum;
    }
}
