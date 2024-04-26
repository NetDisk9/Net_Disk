package com.net.common.exception;


import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.ConstraintViolationException;

/**
 * <p>
 * 全局异常处理器
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-21
 */
@ControllerAdvice  //控制器增强类
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理不可控异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseResult exception(Exception e){
        e.printStackTrace();
        log.error("捕获异常 exception:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.SERVER_ERROR);
    }

    /**
     * 处理可控异常  自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public ResponseResult exception(CustomException e){
        log.error("捕获异常 exception:{}",e.getMessage());
        return ResponseResult.errorResult(e.getResultCodeEnum());
    }
    @ExceptionHandler(AuthException.class)
    @ResponseBody
    public ResponseResult exception(AuthException e){
        log.error("捕获异常 exception:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseResult exception(MethodArgumentNotValidException e){
        log.error("捕获异常 exception:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseResult exception(ConstraintViolationException e){
        e.printStackTrace();
        log.error("捕获异常 ConstraintViolationException:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
    }
    @ExceptionHandler(ParameterException.class)
    @ResponseBody
    public ResponseResult exception(ParameterException e){
        e.printStackTrace();
        log.error("捕获异常 ConstraintViolationException:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
    }
}
