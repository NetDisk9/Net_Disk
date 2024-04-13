package com.net.user.mapper;

import com.net.user.pojo.dto.UserQueryDTO;
import com.net.user.pojo.vo.UserInfoVO;

import java.util.List;

public interface AdminMapper {
    public List<UserInfoVO> listUser(UserQueryDTO userQueryDTO);
    public Integer getTotal(UserQueryDTO userQueryDTO);
}
