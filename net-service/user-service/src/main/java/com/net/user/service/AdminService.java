package com.net.user.service;

import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.exception.AuthException;
import com.net.user.entity.SysUser;
import com.net.user.pojo.dto.UserQueryDTO;
import com.net.user.pojo.vo.PageVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AdminService {
    public PageVO listUser(UserQueryDTO userQueryDTO) throws AuthException;

    void exportUser(List<SysUser> sysUsers, long roleId, HttpServletResponse response);

}
