package com.net.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@TableName("sys_role")
@Data
public class RoleEntity {
    @TableId(value = "role_id")
    private Long roleId;
    private String roleName;
    private String roleCode;
    private Integer roleRank;
}
