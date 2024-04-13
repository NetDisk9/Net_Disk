package com.net.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.enums.RoleEnum;
import com.net.common.exception.AuthException;
import com.net.user.entity.RoleEntity;
import com.net.user.mapper.AdminMapper;
import com.net.user.pojo.dto.UserQueryDTO;
import com.net.user.pojo.vo.PageVO;
import com.net.user.pojo.vo.RoleVO;
import com.net.user.pojo.vo.UserInfoVO;
import com.net.user.service.AdminService;
import com.net.user.service.RoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
@Service
public class AdminServiceImpl implements AdminService {
    @Resource
    AdminMapper adminMapper;
    @Resource
    RoleService roleService;
    @Override
    public PageVO listUser(UserQueryDTO userQueryDTO) throws AuthException {
        PageVO<UserInfoVO> pageVO=new PageVO<>();
        RoleEntity userRoleEntity=roleService.getTopRankRoleEntity(BaseContext.getCurrentId());
        if(userQueryDTO.getRoleId()==null){
            Long nowRoleId=userRoleEntity.getRoleId();
            userQueryDTO.setRoleId(nowRoleId);
            userQueryDTO.setIsAll(1);
        }
        else {
            RoleEntity roleEntity=roleService.getById(userQueryDTO.getRoleId());
            if(roleEntity.getRoleRank()>=userRoleEntity.getRoleRank()){
                throw new AuthException();
            }
        }
        List<UserInfoVO> list=adminMapper.listUser(userQueryDTO);
        pageVO.setList(list);
        pageVO.setLen(list.size());
        pageVO.setTot(getTotal(userQueryDTO));
        for(var temp:list){
            RoleEntity roleEntity=roleService.getTopRankRoleEntity(temp.getUserId());
            RoleVO roleVO=new RoleVO();
            BeanUtil.copyProperties(roleEntity,roleVO);
            temp.setRoleVO(roleVO);
        }
        System.out.println(list);
        return pageVO;
    }
    public Integer getTotal(UserQueryDTO userQueryDTO){
        return adminMapper.getTotal(userQueryDTO);
    }
}
