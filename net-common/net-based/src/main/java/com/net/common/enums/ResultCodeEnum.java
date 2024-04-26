package com.net.common.enums;
/**
 * <p>
 * 状态码枚举
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-21
 */
public enum ResultCodeEnum {
    // 成功段固定为200
    SUCCESS(200,"操作成功"),
    // 用户段
    LOGIN_PASSWORD_ERROR(403,"用户名或密码错误"),
    USERNAME_HASUSED(411,"用户名已被占用"),
    PASSWORD_MISTAKE_EXCESSIVE(421,"密码错误次数过多"),
    PARAM_ERROR(422,"参数格式错误"),
    LOGIN_METHOD_UNSUPPORT(423,"登录方式不支持"),

    // 参数段
    IMAGE_FORMAT_ERROR(432,"图片格式错误"),
    CODE_ERROR(440,"验证码错误"),
    TOKEN_ERROR(442,"token错误"),
    EMAIL_HASUSED(453,"邮箱已被注册"),
    UNAUTHORIZED(401,"权限不足"),
    FILE_NAME_REPEAT(444,"文件名重复"),

    SERVER_ERROR(500,"服务器内部错误"),
    ;


    int code;
    String message;

    ResultCodeEnum(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
