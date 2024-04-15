package com.net.user.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.exception.AuthException;
import com.net.common.exception.CustomException;
import com.net.user.entity.SysUser;
import com.net.user.pojo.dto.UserQueryDTO;
import com.net.user.service.AdminService;
import com.net.user.service.SysUserService;
import com.net.user.util.RegexUtil;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/list")
    public ResponseResult listUser(@RequestBody UserQueryDTO userQueryDTO, int page, int pageSize) throws AuthException {
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

}
