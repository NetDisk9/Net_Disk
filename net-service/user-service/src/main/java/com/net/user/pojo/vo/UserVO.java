package com.net.user.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 * 用户响应
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserVO {
    /**
     * 主键
     */
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
     * 头像
     */
    private String avatar;
}