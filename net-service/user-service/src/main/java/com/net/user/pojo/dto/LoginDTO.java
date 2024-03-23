package com.net.user.pojo.dto;

import lombok.Builder;
import lombok.Data;

/**
 * <p>
 * 注册参数
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-21
 */
@Data
@Builder
public class LoginDTO {
    private Long id;
    /**
     * 用户名称/账号
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 密码
     */
    private String password;

    /**
     * 验证码
     */
    private String code;
    private String deviceName;
    private String deviceOS;
}
