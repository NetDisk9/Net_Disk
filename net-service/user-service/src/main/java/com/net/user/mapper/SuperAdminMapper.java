package com.net.user.mapper;

import com.net.user.pojo.dto.UserQueryDTO;
import com.net.user.pojo.vo.UserInfoVO;

import java.util.List;

public interface SuperAdminMapper {
    public List<UserInfoVO> listAllUser(UserQueryDTO userQueryDTO);
    public List<UserInfoVO> listAdmin(UserQueryDTO userQueryDTO);
    public List<UserInfoVO> listUser(UserQueryDTO userQueryDTO);

    public Integer getAllTotal(UserQueryDTO userQueryDTO);
    public Integer getUserTotal(UserQueryDTO userQueryDTO);
    public Integer getAdminTotal(UserQueryDTO userQueryDTO);

}
