package com.net.user.service;

import com.net.common.dto.ResponseResult;
import com.net.user.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.net.user.pojo.dto.RegisterDTO;
import com.net.user.pojo.dto.UpdatePasswordDTO;
import com.net.user.pojo.dto.UserDTO;

import java.util.List;

/**
 * <p>
 * 用户信息表 服务类
 * </p>
 *
 * @author 倪圳褒
 * @since 2024-03-15
 */
public interface SysUserService extends IService<SysUser> {
    ResponseResult insertRegisterInfo(RegisterDTO registerDTO);

    ResponseResult checkUserIDExists(Long userID);

    ResponseResult getUserByEmailAndPassword(String email, String password);

    ResponseResult getUserByUserIdAndPassword(Long userId, String password);

    ResponseResult getUserByUsernameAndPassword(String username, String password);

    SysUser getUserByEmail(String email);

    String getUserLoginCode(String email);

    void deleteUserLoginCode(String email);

    ResponseResult updatePassword(UpdatePasswordDTO updatePasswordDTO);

    ResponseResult updateUserInfo(UserDTO userDTO);

    ResponseResult getUserInfo();

    ResponseResult getLoginMethod();

    ResponseResult updateLoginMethod(String methods);

    ResponseResult updateAvatar(String data);

    List<SysUser> addBatchUserByAdmin(int count, long roleId);

    ResponseResult addUserByAdmin(String username, String password, Long roleId);

    boolean checkUsernameExists(String username);

    boolean checkEmailExists(String email);

    ResponseResult forgetPassword(String email, String newPassword);

    ResponseResult updateVIPDuration(Long userId, int duration, boolean isRenew, boolean updateNowOrEnd);
}


