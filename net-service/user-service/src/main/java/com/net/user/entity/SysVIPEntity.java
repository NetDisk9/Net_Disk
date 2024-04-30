package com.net.user.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("sys_vip")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysVIPEntity {
    @TableId(value = "vip_id")
    private Long vipId;

    private Long userId;

    private String updateTime;

    private String endTime;

}
