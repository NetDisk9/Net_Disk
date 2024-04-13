package com.net.user.service;

import com.net.user.pojo.dto.UserQueryDTO;
import com.net.user.pojo.vo.PageVO;

public interface SuperAdminService {
    public PageVO listUser(UserQueryDTO userQueryDTO);
}
