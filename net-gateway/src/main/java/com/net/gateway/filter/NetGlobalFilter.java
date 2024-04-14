package com.net.gateway.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.net.api.client.AuthClient;
import com.net.common.context.BaseContext;
import com.net.common.dto.ResponseResult;
import com.net.common.enums.ResultCodeEnum;
import com.net.common.exception.CustomException;
import com.net.common.util.JWTUtil;
import com.net.redis.constant.RedisConstants;
import com.net.redis.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.util.List;
import java.util.Set;

@Component
public class NetGlobalFilter implements GlobalFilter, Ordered {
    @Value("${exclude.path}")
    private String[] excludePath;
    @Value("${redirect.path}")
    private String[] redirectPath;
    @Resource
    private RedisUtil redisUtil;
    @Autowired
    private AuthClient authClient;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
//        return chain.filter(exchange);
        if (isExclude(request.getPath().toString())) {
            System.out.println("放行" + request.getPath());
//            URI newUri = UriComponentsBuilder.newInstance()
//                    .path("/super/mk1")
//                    .build()
//                    .toUri();
//            ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().uri(newUri).build();
//            ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
//            System.out.println(modifiedExchange.getRequest().getPath());
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
            e.printStackTrace();
//                response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
//                response.getHeaders().setLocation(URI.create(RedirectConstants.TOKEN_ERROR_REDIRECT_URL));
            return finishResponse(exchange.getResponse(),ResponseResult.errorResult(ResultCodeEnum.TOKEN_ERROR));
        }
        String path=request.getPath().toString();
        System.out.println(path);
        BaseContext.setCurrentId(Long.parseLong(userId));
        if(!havePermission(userId,request.getPath().toString())){
            return finishResponse(exchange.getResponse(),ResponseResult.errorResult(ResultCodeEnum.UNAUTHORIZED));
        }
        if(canRedirect(path)){
            if(Boolean.parseBoolean(authClient.isSuperAdministrator(userId))){
                path=path.replace("admin","super");
                return chain.filter(createRedirectRequest(exchange,path));
            }
        }
        exchange.mutate()
                .request(builder -> builder.header("user-info", userId))
                .build();

        System.out.println(exchange.getRequest().getPath());
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }

    private boolean isExclude(String antPath) {
        for (String pathPattern : excludePath) {
            //antPathMatcher来匹配 类似/search/**
            if(antPathMatcher.match(pathPattern, antPath)){
                return true;
            }
        }
        return false;
    }
    private boolean canRedirect(String antPath) {
        for (String pathPattern : redirectPath) {
            //antPathMatcher来匹配 类似/search/**
            if(antPathMatcher.match(pathPattern, antPath)){
                return true;
            }
        }
        return false;
    }
    private ServerWebExchange createRedirectRequest(ServerWebExchange exchange,String path){
        URI newUri = UriComponentsBuilder.newInstance()
        .path(path)
        .build()
        .toUri();
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate().uri(newUri).build();
        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
        return modifiedExchange;
    }
    private Mono<Void> finishResponse(ServerHttpResponse response,ResponseResult result)  {
        try {
            DataBufferFactory bufferFactory = response.bufferFactory();
            ObjectMapper objectMapper = new ObjectMapper();
            DataBuffer wrap = bufferFactory.wrap(objectMapper.writeValueAsBytes(result));
            response.setStatusCode(HttpStatus.OK);
//                response.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
//                response.getHeaders().setLocation(URI.create(RedirectConstants.TOKEN_ERROR_REDIRECT_URL));
            return response.writeWith(Mono.fromSupplier(() -> wrap));
        }
        catch (Exception e){
            e.printStackTrace();
            return response.setComplete();
        }
    }
    private boolean havePermission(String userId,String path){
        String permissionKey=RedisConstants.USER_PERMISSION+userId;
        if(redisUtil.hasKey(permissionKey)){
            Set set=redisUtil.sGet(permissionKey);
            return set.contains(path);
        }
        return Boolean.parseBoolean(authClient.havePermission(path));
    }

}
