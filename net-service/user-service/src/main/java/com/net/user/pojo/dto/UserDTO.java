package com.net.user.pojo.dto;

import lombok.Builder;
import lombok.Data;

/**
 * <p>
 * 用户参数
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-21
 */
@Data
@Builder
public class UserDTO {

    /**
     * 用户名称/账号
     */
    private String username;

}
