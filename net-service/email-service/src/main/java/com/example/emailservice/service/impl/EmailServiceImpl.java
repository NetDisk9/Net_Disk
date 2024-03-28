package com.example.emailservice.service.impl;

import com.baomidou.mybatisplus.extension.api.R;
import com.example.emailservice.service.EmailService;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.redis.constant.RedisConstants;
import com.net.redis.utils.RedisUtil;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Service
@ConfigurationProperties(prefix = "properties")
public class EmailServiceImpl implements EmailService {
    @Resource
    JavaMailSender javaMailSender;
    Map<String,String> set;
    @Resource
    RedisUtil redisUtil;
    @Override
    public ResponseResult sendCode(String email, String type) {

        if(!set.containsKey(type)){
            return ResponseResult.errorResult(ResultCodeEnum.CODE_ERROR);
        }
        int code=RandomUtils.nextInt((int)1e5,(int)1e6-1);
        String key=set.get(type);
        if(redisUtil.get(key+email)!=null){
            return ResponseResult.errorResult(442,"请求次数超限");
        }
        redisUtil.set(key+email,code, 5*60);
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom("872919781@qq.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("title");
        simpleMailMessage.setText(code+"");
        try {
            javaMailSender.send(simpleMailMessage);
        }
        catch (Exception e){
            return ResponseResult.errorResult(422,"参数错误");
        }
        return ResponseResult.okResult(200,"响应成功");
    }

    public void setSet(Map<String, String> set) {
        this.set = set;
    }
}
