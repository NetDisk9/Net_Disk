package com.net.user.pojo.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserQueryDTO {
    private String username;
    private Long userId;
    private Long roleId;
    private String email;
    private Integer status;
    private String begTime;
    private String endTime;
    private Integer usernameOrder;
    private Integer idOrder;
    private Integer emailOrder;
    private Integer statusOrder;
    private Integer index;
    private Integer pageSize;

}
