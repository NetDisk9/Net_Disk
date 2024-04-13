package com.net.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.net.common.enums.RoleEnum;
import com.net.user.entity.RoleEntity;
import com.net.user.mapper.SuperAdminMapper;
import com.net.user.pojo.dto.UserQueryDTO;
import com.net.user.pojo.vo.PageVO;
import com.net.user.pojo.vo.RoleVO;
import com.net.user.pojo.vo.UserInfoVO;
import com.net.user.service.RoleService;
import com.net.user.service.SuperAdminService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SuperAdminServiceImpl implements SuperAdminService {
    @Resource
    SuperAdminMapper superAdminMapper;
    @Resource
    RoleService roleService;
    @Override
    public PageVO listUser(UserQueryDTO userQueryDTO) {
        PageVO<UserInfoVO> pageVO=new PageVO<>();
        List<UserInfoVO> userInfoVOList;
        int tot;
        if(userQueryDTO.getRoleId()==null){
            userInfoVOList=superAdminMapper.listAllUser(userQueryDTO);
            tot=superAdminMapper.getAllTotal(userQueryDTO);
            for(var temp:userInfoVOList){

            }
        }
        else if(RoleEnum.USER.getRoleId().equals(userQueryDTO.getRoleId())){
            userInfoVOList=superAdminMapper.listUser(userQueryDTO);
            tot=superAdminMapper.getUserTotal(userQueryDTO);
            RoleEntity roleEntity=roleService.getRoleVOByName(RoleEnum.USER.getName());
            RoleVO roleVO=new RoleVO();
            BeanUtil.copyProperties(roleEntity,roleVO);
            for(var temp:userInfoVOList){
                temp.setRoleVO(roleVO);
            }
        }
        else{
            userInfoVOList=superAdminMapper.listAdmin(userQueryDTO);
            tot=superAdminMapper.getAdminTotal(userQueryDTO);
            RoleEntity roleEntity=roleService.getRoleVOByName(RoleEnum.ADMIN.getName());
            RoleVO roleVO=new RoleVO();
            BeanUtil.copyProperties(roleEntity,roleVO);
            for(var temp:userInfoVOList){
                temp.setRoleVO(roleVO);
            }
        }
        pageVO.setList(userInfoVOList);
        pageVO.setTot(tot);
        pageVO.setLen(userInfoVOList.size());
        return pageVO;
    }

}
