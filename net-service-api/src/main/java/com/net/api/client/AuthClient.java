package com.net.api.client;

import com.net.common.context.BaseContext;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service")
public interface AuthClient {
    @GetMapping("/permission/authenticate")
    public String havePermission(@RequestParam String path);
    @GetMapping("/role/issuper")
    public String isSuperAdministrator(@RequestParam String id);
}
