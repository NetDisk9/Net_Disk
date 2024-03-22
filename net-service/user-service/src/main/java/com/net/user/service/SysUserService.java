package com.net.user.service;

import com.net.common.dto.ResponseResult;
import com.net.user.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-15
 */
public interface SysUserService extends IService<SysUser> {
    public ResponseResult getUserIdByEmailAndPassword(String email, String password);
    public ResponseResult getUserIdByUserIdAndPassword(Long userId,String password);
    public ResponseResult getUserIdByUsernameAndPassword(String username,String password);
    public Long getUserIdByEmail(String email);
    public String getUserLoginCode(String email);
}
