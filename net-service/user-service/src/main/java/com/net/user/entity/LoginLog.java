package com.net.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@TableName("user_log")
@Data
public class LoginLog {
    @TableId(value = "log_id", type = IdType.ASSIGN_ID)
    private int logId;
    private Long userId;
    private String deviceName;
    @TableField("device_os")
    private String deviceOS;
    private LocalDateTime loginTime;
    private String loginAddress;
    private String loginIp;

    public LoginLog(Long userId, String deviceName, String deviceOS, LocalDateTime loginTime, String loginAddress, String loginIp) {
        this.userId = userId;
        this.deviceName = deviceName;
        this.deviceOS = deviceOS;
        this.loginTime = loginTime;
        this.loginAddress = loginAddress;
        this.loginIp = loginIp;
    }

    public LoginLog() {
    }
}
