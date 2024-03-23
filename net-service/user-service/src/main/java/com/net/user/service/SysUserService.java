package com.net.user.service;

import com.net.common.dto.ResponseResult;
import com.net.user.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.net.user.pojo.dto.UpdatePasswordDTO;
import com.net.user.pojo.dto.UserDTO;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-15
 */
public interface SysUserService extends IService<SysUser> {
    ResponseResult getUserIdByEmailAndPassword(String email, String password);
    ResponseResult getUserIdByUserIdAndPassword(Long userId, String password);
    ResponseResult getUserIdByUsernameAndPassword(String username, String password);
    Long getUserIdByEmail(String email);
    String getUserLoginCode(String email);
    void deleteUserLoginCode(String email);
    ResponseResult updatePassword(UpdatePasswordDTO updatePasswordDTO);
    ResponseResult updateUserInfo(UserDTO userDTO);
    ResponseResult getUserInfo();

}

