package com.example.emailservice.service.impl;

import com.baomidou.mybatisplus.extension.api.R;
import com.example.emailservice.service.EmailService;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.exception.ParameterException;
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
        String key=set.get(type);
        int code=RandomUtils.nextInt((int)1e5,(int)1e6-1);
        Long expire=redisUtil.getExpire(key+email);
        if(expire>0&&RedisConstants.CODE_TTL-expire<=60){
            return ResponseResult.errorResult(442,"请求次数超限");
        }
        redisUtil.set(key+email,code, RedisConstants.CODE_TTL);
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setFrom("872919781@qq.com");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("title");
        simpleMailMessage.setText(code+"");
        try {
            javaMailSender.send(simpleMailMessage);
        }
        catch (Exception e){
            e.printStackTrace();
            return ResponseResult.errorResult(422,"参数错误");
        }
        return ResponseResult.okResult(code);
    }

    @Override
    public boolean checkCode(String email, String code, String type) {
        if(!set.containsKey(type)){
            return false;
        }
        String key=set.get(type);
        String value =  redisUtil.get(key + email).toString();
        return code.equals(value);
    }

    @Override
    public void saveRes(String email, String code, String type) throws ParameterException {
        if(!set.containsKey(type)){
            throw  new ParameterException();
        }
        String key=set.get(type);
        redisUtil.set(key+"res:"+email,1,RedisConstants.LOGIN_USER_TTL);
    }

    @Override
    public void deleteCode(String email, String type) {
        if(!set.containsKey(type)){
            throw  new ParameterException();
        }
        String key=set.get(type);
        redisUtil.del(key+email);
    }

    public void setSet(Map<String, String> set) {
        this.set = set;
    }
}
