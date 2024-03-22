package com.net.user.pojo.dto;

import lombok.Builder;
import lombok.Data;

/**
 * <p>
 * 用户响应
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-15
 */
@Data
@Builder
public class UpdatePasswordDTO {

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;
}