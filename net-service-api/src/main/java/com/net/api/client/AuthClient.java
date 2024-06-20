package com.net.api.client;

import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service")
public interface AuthClient {
    @GetMapping("/permission/authenticate")
    public Boolean havePermission(@RequestParam String path);
    @GetMapping("/role/issuper")
    public Boolean isSuperAdministrator(@RequestParam String id);
    @GetMapping("/role/isvip")
    public Boolean isVIP();
    @GetMapping("/user/info")
    public ResponseResult getUserInfo();
}
