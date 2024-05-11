package com.net.user.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoVO {
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String email;
    private String loginTime;
    private Integer status;
    private RoleVO roleVO;
    private VIPVO vipVO;
}
