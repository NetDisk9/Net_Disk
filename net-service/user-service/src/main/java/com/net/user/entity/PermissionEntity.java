package com.net.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
@TableName("sys_permission")
@Data
@AllArgsConstructor
public class PermissionEntity {
    @TableId(value = "permission_id",type = IdType.AUTO)
    private Long permissionId;
    private Long pid;
    private String permissionValue;
    private String name;
    private String path;
    private String component;

}
