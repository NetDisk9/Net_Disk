package com.net.user;

import com.net.common.util.SHAUtil;
import com.net.redis.utils.RedisUtil;
import com.net.user.entity.SysUser;
import com.net.user.service.SysUserService;
import com.net.user.util.RegexUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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
    public void test(){
        System.out.println(RegexUtil.checkPasswordValid("consequat"));
        System.out.println(RegexUtil.checkPasswordValid("123456788"));
        System.out.println(RegexUtil.checkPasswordValid("WEWAEWAEAE"));
        System.out.println(RegexUtil.checkPasswordValid("Baozi0318"));
        System.out.println(RegexUtil.checkPasswordValid("BA123OZI23"));
        System.out.println(RegexUtil.checkPasswordValid("BA@ZIASD"));
        System.out.println(RegexUtil.checkPasswordValid("BAZISASD"));
    }

}
