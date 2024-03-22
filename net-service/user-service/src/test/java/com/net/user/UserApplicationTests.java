package com.net.user;

import com.net.redis.utils.RedisUtil;
import com.net.user.entity.SysUser;
import com.net.user.service.SysUserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class UserApplicationTests {
    @Resource
    SysUserService userService;
    @Resource
    RedisUtil redisUtil;

    @Test
    public void redisTest(){
        redisUtil.set("abc",123);
        System.out.println(redisUtil.get("abc"));
    }
    @Test
    public void add() {
        userService.save(
                SysUser.builder().sex(0).
                email("11215017128@qq.com").avatar("null").password("12345").
                username("baozi").status(0).nickname("包子").build()
        );

    }

}
