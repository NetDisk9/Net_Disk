package com.net.user.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    /**
     * 用户名
     */
    private String username;
    /**
     * 用户Id
     */
    private String userId;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 状态 0启用 1禁用
     */
    private Integer status;
}
