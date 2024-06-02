package com.net.common.interceptor;

import com.net.common.context.BaseContext;
import com.net.common.util.IPUtil;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInfoInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String info = request.getHeader("user-info");
        if (info != null && info.length() != 0) {
            BaseContext.setCurrentId(Long.parseLong(info));
            BaseContext.setCurrentIp(IPUtil.getIp(request));
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.clear();
    }
}
