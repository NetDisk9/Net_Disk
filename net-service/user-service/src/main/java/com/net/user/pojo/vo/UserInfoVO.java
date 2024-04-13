package com.net.user.pojo.vo;

import lombok.Data;

@Data
public class UserInfoVO {
    private  Long userId;
    private  String username;
    private  String email;
    private  String loginTime;
    private  Integer status;
    private  RoleVO roleVO;
}
