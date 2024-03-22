package com.net.user.pojo.vo;

import lombok.Builder;
import lombok.Data;

/**
 * <p>
 * 设备响应
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-15
 */
@Data
@Builder
public class DeviceVO {
    /**
     * 设备名
     */
    private String DeviceName;
    /**
     * 操作系统
     */
    private String DeviceOS;
    /**
     * 登录类型
     */
    private String LoginType;
    /**
     * 登录时间
     */
    private String LoginTime;
}
