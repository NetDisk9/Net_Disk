package com.net.user.service;

import com.net.common.exception.AuthException;
import com.net.user.pojo.dto.UserQueryDTO;
import com.net.user.pojo.vo.PageVO;
import com.net.user.pojo.vo.UserInfoVO;

import java.util.List;

public interface AdminService {
    public PageVO listUser(UserQueryDTO userQueryDTO) throws AuthException;
}
