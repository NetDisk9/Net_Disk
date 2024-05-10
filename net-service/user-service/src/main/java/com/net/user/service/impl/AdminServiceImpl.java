package com.net.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.enums.RoleEnum;
import com.net.common.exception.AuthException;
import com.net.common.exception.CustomException;
import com.net.user.constant.UserConstants;
import com.net.user.entity.RoleEntity;
import com.net.user.entity.SysUser;
import com.net.user.entity.SysVIPEntity;
import com.net.user.mapper.AdminMapper;
import com.net.user.pojo.dto.UserQueryDTO;
import com.net.user.pojo.vo.PageVO;
import com.net.user.pojo.vo.RoleVO;
import com.net.user.pojo.vo.UserInfoVO;
import com.net.user.pojo.vo.VIPVO;
import com.net.user.service.AdminService;
import com.net.user.service.RoleService;
import com.net.user.service.SysVIPService;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Resource
    AdminMapper adminMapper;
    @Resource
    RoleService roleService;
    @Resource
    SysVIPService vipService;

    @Override
    public PageVO listUser(UserQueryDTO userQueryDTO) throws AuthException {
        PageVO<UserInfoVO> pageVO = new PageVO<>();
        RoleEntity userRoleEntity = roleService.getTopRankRoleEntity(BaseContext.getCurrentId());
        if (userQueryDTO.getRoleId() == null) {
            Long nowRoleId = userRoleEntity.getRoleId();
            userQueryDTO.setRoleId(nowRoleId);
            userQueryDTO.setIsAll(1);
        } else {
            RoleEntity roleEntity = roleService.getById(userQueryDTO.getRoleId());
            if (roleEntity.getRoleRank() >= userRoleEntity.getRoleRank()) {
                throw new AuthException();
            }
        }
        List<UserInfoVO> list = adminMapper.listUser(userQueryDTO);
        pageVO.setList(list);
        pageVO.setLen(list.size());
        pageVO.setTot(getTotal(userQueryDTO));
        for (var temp : list) {
            RoleEntity roleEntity = roleService.getTopRankRoleEntity(temp.getUserId());
            if(!RoleEnum.USER.getName().equals(roleEntity.getRoleName())){
                SysVIPEntity vip = vipService.getOne(Wrappers.<SysVIPEntity>lambdaQuery().eq(SysVIPEntity::getUserId, temp.getUserId()));
                temp.setVipVO(BeanUtil.copyProperties(vip, VIPVO.class));
            }
            RoleVO roleVO = new RoleVO();
            BeanUtil.copyProperties(roleEntity, roleVO);
            temp.setRoleVO(roleVO);
        }
        return pageVO;
    }

    @Override
    public void exportUser(List<SysUser> userList, long roleId, HttpServletResponse response) {
        LocalDate now = LocalDate.now();
        //2. 通过POI将数据写入到Excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("新增用户数据模版.xlsx");

        try {
            if (in == null) throw new RuntimeException("没有找到模版");
            //基于模板文件创建一个新的Excel文件
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //获取表格文件的Sheet页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //填充数据--时间
            sheet.getRow(1).getCell(1).setCellValue("新增时间：" + now);

            //获得第4行
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(userList.size());
            RoleEntity role = roleService.getById(roleId);
            row.getCell(4).setCellValue(role.getRoleCode());

            //填充明细数据
            for (int i = 0; i < userList.size(); i++) {
                //获得某一行
                row = sheet.getRow(6 + i);
                row.getCell(1).setCellValue(userList.get(i).getId());
                row.getCell(2).setCellValue(UserConstants.DEAULT_PASSWORD);
                row.getCell(3).setCellValue(userList.get(i).getPassword());
                row.getCell(4).setCellValue(role.getRoleName());
            }

            // 设置响应
            response.setContentType("application/vnd.excel");
            response.setCharacterEncoding("utf-8");
            String fileName = URLEncoder.encode("新增用户数据", StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");

            ServletOutputStream out = response.getOutputStream();
            excel.write(out);

            //关闭资源
            out.close();
            excel.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw new CustomException(ResultCodeEnum.SERVER_ERROR);
        }
    }


    public Integer getTotal(UserQueryDTO userQueryDTO) {
        return adminMapper.getTotal(userQueryDTO);
    }
}
