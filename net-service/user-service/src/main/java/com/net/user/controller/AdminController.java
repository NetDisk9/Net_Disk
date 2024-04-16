package com.net.user.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.exception.AuthException;
import com.net.common.exception.CustomException;
import com.net.user.entity.SysUser;
import com.net.user.pojo.dto.UserQueryDTO;
import com.net.user.service.AdminService;
import com.net.user.service.RoleService;
import com.net.user.service.SysUserService;
import com.net.user.util.RegexUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RequestMapping("/admin")
@RestController
public class AdminController extends BaseAdminController {
    @Resource
    AdminService adminService;
    @Resource
    SysUserService userService;
    @Resource
    RoleService roleService;

    @PostMapping("/list")
    public ResponseResult listUser(@RequestBody UserQueryDTO userQueryDTO, Integer page, Integer pageSize) throws AuthException {
        if (!initQuery(userQueryDTO, page, pageSize)) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        return ResponseResult.okResult(adminService.listUser(userQueryDTO));
    }

    @PostMapping("/add/all/user")
    @Transactional(rollbackFor = Exception.class)
    public void addBatchUser(int count, long roleId, HttpServletResponse response) {
        if (count <= 0 || roleId < 0) throw new CustomException(ResultCodeEnum.PARAM_ERROR);
        List<SysUser> sysUsers = userService.addBatchUserByAdmin(count, roleId);
        adminService.exportUser(sysUsers, roleId, response);
    }

    @PostMapping("/add/user")
    public ResponseResult addUser(String username, String password, Long roleId) {
        if (StringUtils.isBlank(username) || StringUtils.isBlank(password) ||
                roleId == null || !RegexUtil.checkPasswordValid(password))
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);

        return userService.addUserByAdmin(username, password, roleId);
    }

    @PutMapping("/role/update")
    public ResponseResult updateUserRole(Long userId, Long roleId) {
        if (userId == null || roleId == null) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        ResponseResult checkUserIDResult = userService.checkUserIDExists(userId);
        if (checkUserIDResult.getCode() != 200) {
            return checkUserIDResult;
        }
        ResponseResult checkAuthorityResult = roleService.checkAuthority(userId, roleId);
        if (checkAuthorityResult.getCode() != 200) {
            return checkAuthorityResult;
        }
        return roleService.updateUserRole(userId, roleId);
    }

    @PutMapping("/update")
    public ResponseResult updatePassword(Long userId) {
        if (userId == null) {
            return ResponseResult.errorResult(ResultCodeEnum.PARAM_ERROR);
        }
        ResponseResult checkUserIDResult = userService.checkUserIDExists(userId);
        if (checkUserIDResult.getCode() != 200) {
            return checkUserIDResult;
        }
        ResponseResult checkAuthorityResult = roleService.checkAuthorityForPassword(userId);
        if (checkAuthorityResult.getCode() != 200) {
            return checkAuthorityResult;
        }
        return roleService.updateUserPassword(userId);
    }

    @GetMapping("/role/list/option")
    public ResponseResult getModifiableUserRole() {
        return roleService.getModifiableUserRole();
    }

}
