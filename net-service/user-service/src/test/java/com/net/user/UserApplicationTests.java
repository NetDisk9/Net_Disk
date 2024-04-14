package com.net.user;

import com.net.common.util.SHAUtil;
import com.net.redis.utils.RedisUtil;
import com.net.user.entity.SysUser;
import com.net.user.service.SysUserService;
import com.net.user.util.RegexUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;

//@SpringBootTest
public class UserApplicationTests {
//    @Resource
//    SysUserService userService;
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
//    @Test
//    public void testPassword(){
//        System.out.println(SHAUtil.encrypt("Baozi0318"));
//    }
    @Test
    public void test() throws IOException {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("新增用户数据模板.xlsx");

        if(in==null)throw new RuntimeException("没有找到模版");
        //基于模板文件创建一个新的Excel文件
        XSSFWorkbook excel = new XSSFWorkbook(in);
        //获取表格文件的Sheet页
        XSSFSheet sheet = excel.getSheet("Sheet1");

        //获得第4行
        XSSFRow row = sheet.getRow(6);
        System.out.println(row.getCell(1).getNumericCellValue());
    }

}
