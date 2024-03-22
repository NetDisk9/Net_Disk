package com.net.common.dto;


import com.net.common.enums.ResultCodeEnum;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 通用的结果返回类
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-21
 */
public class ResponseResult<T> implements Serializable {

    private Integer code;

    private String message;

    private T data;

    public ResponseResult() {
        this.code = 200;
    }

    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.message = msg;
        this.data = data;
    }

    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public static ResponseResult okResult(int code, String msg) {
        ResponseResult result = new ResponseResult();
        return result.ok(code, null, msg);
    }

    public static ResponseResult okResult(Object data) {
        ResponseResult result = setAppHttpCodeEnum(ResultCodeEnum.SUCCESS, ResultCodeEnum.SUCCESS.getMessage());
        if(data!=null) {
            result.setData(data);
        }
        return result;
    }

    public static ResponseResult okResult(ResultCodeEnum enums) {
        ResponseResult result = setAppHttpCodeEnum(enums,enums.getMessage());
        return result;
    }
    public static ResponseResult errorResult(ResultCodeEnum enums){
        return setAppHttpCodeEnum(enums,enums.getMessage());
    }

    public static ResponseResult errorResult(ResultCodeEnum enums, String errorMessage){
        return setAppHttpCodeEnum(enums,errorMessage);
    }

    public static ResponseResult errorResult(int code, String msg) {
        ResponseResult result = new ResponseResult();
        return result.error(code, msg);
    }

    public static ResponseResult setAppHttpCodeEnum(ResultCodeEnum enums){
        return okResult(enums.getCode(),enums.getMessage());
    }

    private static ResponseResult setAppHttpCodeEnum(ResultCodeEnum enums, String errorMessage){
        return okResult(enums.getCode(),errorMessage);
    }

    public ResponseResult<?> error(Integer code, String msg) {
        this.code = code;
        this.message = msg;
        return this;
    }

    public ResponseResult<?> ok(Integer code, T data) {
        this.code = code;
        this.data = data;
        return this;
    }

    public ResponseResult<?> ok(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.message = msg;
        return this;
    }

    public ResponseResult<?> ok(T data) {
        this.data = data;
        return this;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }



    public static void main(String[] args) {
        //前置
        ResultCodeEnum success = ResultCodeEnum.SUCCESS;
        System.out.println(success.getCode());
        System.out.println(success.getMessage());
        System.out.println("=====================================");

        //查询一个对象
        Map map = new HashMap();
        map.put("name","zhangsan");
        map.put("age",18);
        ResponseResult result = ResponseResult.okResult(map);
        System.out.println(JSON.toJSONString(result));
        System.out.println("=====================================");


        //新增，修改，删除  在项目中统一返回成功即可
        ResponseResult result1 = ResponseResult.okResult(ResultCodeEnum.SUCCESS);
        System.out.println(JSON.toJSONString(result1));
        System.out.println("=====================================");


        //根据不用的业务返回不同的提示信息  比如：当前操作需要登录、参数错误
        ResponseResult result2 = ResponseResult.errorResult(ResultCodeEnum.NEED_LOGIN,"自定义提示信息");
        System.out.println(JSON.toJSONString(result2));
        System.out.println("=====================================");

        //查询分页信息
        PageResponseResult responseResult = new PageResponseResult(1,5,50);
        List list = new ArrayList();
        list.add("baozi");
        list.add("bao");
        responseResult.setData(list);
        System.out.println(JSON.toJSONString(responseResult));

    }

}
