package com.net.user.controller;

import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.util.DateFormatUtil;
import com.net.user.pojo.dto.UserQueryDTO;

import java.util.Date;

public class BaseAdminController {
    public boolean initQuery(UserQueryDTO userQueryDTO, Integer page,Integer pageSize){
        if(page==null||pageSize==null||page<=0||pageSize<=0){
            return false;
        }
        userQueryDTO.setIndex((page-1)*pageSize);
        userQueryDTO.setPageSize(pageSize);
        try {
            Date begDate= DateFormatUtil.trans(userQueryDTO.getBegTime());
            Date endDate= DateFormatUtil.trans(userQueryDTO.getEndTime());
            if(begDate!=null&&endDate!=null){
                if(endDate.compareTo(begDate)==-1){
                    return false;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
