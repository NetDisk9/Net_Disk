package com.net.common.exception;


import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

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
        log.error("捕获异常 CustomException:{}",e.getMessage());
        return ResponseResult.errorResult(e.getResultCodeEnum(),e.getMessage());
    }
    @ExceptionHandler(AuthException.class)
    @ResponseBody
    public ResponseResult exception(AuthException e){
        log.error("捕获异常 AuthException:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.UNAUTHORIZED,getMessage(e,ResultCodeEnum.UNAUTHORIZED.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseResult exception(MethodArgumentNotValidException e){
        log.error("捕获异常 MethodArgumentNotValidException:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
    }
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseResult exception(ConstraintViolationException e){
        log.error("捕获异常 ConstraintViolationException:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR,"必要参数为空");
    }
    @ExceptionHandler(MissingServletRequestPartException.class)
    @ResponseBody
    public ResponseResult exception(MissingServletRequestPartException e){
        log.error("捕获异常 MissingServletRequestPartException:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR,"必要参数为空");
    }

    @ExceptionHandler(ParameterException.class)
    @ResponseBody
    public ResponseResult exception(ParameterException e){
        log.error("捕获异常 ParameterException:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR,getMessage(e,ResultCodeEnum.PARAM_ERROR.getMessage()));
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseResult exception(MethodArgumentTypeMismatchException e){
        log.error("捕获异常 MethodArgumentTypeMismatchException:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR,"参数类型不匹配");

    }
    @ExceptionHandler(ChunkErrorException.class)
    @ResponseBody
    public ResponseResult exception(ChunkErrorException e){
        log.error("捕获异常 ChunkErrorException:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR,"文件校验异常");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseResult exception(HttpRequestMethodNotSupportedException e){
        log.error("捕获异常 ChunkErrorException:{}",e.getMessage());
        return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR,"请求类型不匹配");
    }
    private String getMessage(Exception e,String defaultMessage){
        if(e.getMessage()!=null&&e.getMessage().length()!=0){
            return e.getMessage();
        }
        return defaultMessage;
    }
}
