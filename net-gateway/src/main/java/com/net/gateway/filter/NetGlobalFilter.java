package com.net.gateway.filter;

import com.net.common.enums.ResultCodeEnum;
import com.net.common.exception.CustomException;
import com.net.gateway.util.JWTUtil;
import com.net.redis.constant.RedisConstants;
import com.net.redis.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.List;

@Component
public class NetGlobalFilter implements GlobalFilter, Ordered {
    @Value("${exclude.path}")
    private String[] excludePath;
    @Resource
    private RedisUtil redisUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
//        return chain.filter(exchange);
        if (isExclude(request.getPath().toString())) {
            System.out.println("放行" + request.getPath());
            return chain.filter(exchange);
        }
        List<String> list = request.getHeaders().get("authorization");
        String token = null;
        if (list != null && !list.isEmpty()) {
            token = list.get(0);
        }
        String userId;
        System.out.println(token);
        try {
            // 从redis中获取相同的token
            String redisToken = (String) redisUtil.get(RedisConstants.LOGIN_USER_KEY + token);
            if (redisToken == null || !redisToken.equals(token)) {
                // token已经失效了
                System.out.println("token失效");
                throw new CustomException(ResultCodeEnum.TOKEN_ERROR);
            }
            userId = JWTUtil.parseJWT(token);
            System.out.println(userId);
        } catch (Exception e) {

            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        exchange.mutate()
                .request(builder -> builder.header("user-info", userId))
                .build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    public boolean isExclude(String path) {
        for (int i = 0; i < excludePath.length; i++)
            if (path.startsWith(excludePath[i]))
                return true;
        return false;
    }
}
