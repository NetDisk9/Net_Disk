package com.net.user;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.net.common.context.BaseContext;
import com.net.common.enums.FileTypeEnum;
import com.net.user.service.LoginLogService;
import com.net.user.service.PermissionService;
import com.net.user.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
public class UserApplicationTests {
    @Resource
    SysUserService userService;
    @Resource
    LoginLogService loginLogService;
    @Resource
    PermissionService permissionService;
//    @Resource
//    RedisUtil redisUtil;
//
//    @Test
//    public void redisTest(){
//        redisUtil.set("abc",123);
//        System.out.println(redisUtil.get("abc"));
//    }
//    @Test
//    public void add() {
//        userService.save(
//                SysUser.builder().sex(0).
//                email("11215017128@qq.com").avatar("null").password("12345").
//                username("baozi").status(0).nickname("包子").build()
//        );
//    }
    @Test
    public void testPassword(){
        System.out.println(FileTypeEnum.IMAGE.getExtensions());
    }
//    @Test
//    public void test() throws IOException {
//        InputStream in = this.getClass().getClassLoader().getResourceAsStream("新增用户数据模板.xlsx");
//
//        if(in==null)throw new RuntimeException("没有找到模版");
//        //基于模板文件创建一个新的Excel文件
//        XSSFWorkbook excel = new XSSFWorkbook(in);
//        //获取表格文件的Sheet页
//        XSSFSheet sheet = excel.getSheet("Sheet1");
//
//        //获得第4行
//        XSSFRow row = sheet.getRow(6);
//        System.out.println(row.getCell(1).getNumericCellValue());
//    }

    @Test
    public void testDate(){
        BaseContext.setCurrentId(1L);
        System.out.println(loginLogService.getDevice().getData());
    }
    @Test
    public void testP(){


    }

    public static void main(String[] args) throws JsonProcessingException {
        System.out.println(Boolean.TRUE.toString());
        System.out.println(new ObjectMapper().writeValueAsString(Boolean.TRUE));
        System.out.println("true");
        System.out.println(Boolean.parseBoolean("true"));
    }
}
