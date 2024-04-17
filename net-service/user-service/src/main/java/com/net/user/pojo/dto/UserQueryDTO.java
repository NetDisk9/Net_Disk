package com.net.user.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
    private Integer isAll=0;

}
