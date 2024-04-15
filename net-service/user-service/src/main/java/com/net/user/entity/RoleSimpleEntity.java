package com.net.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Comparator;

@TableName("sys_role")
@Data
public class RoleSimpleEntity{
    @TableId(value = "role_id")
    private Long roleId;
    private String roleName;
}
