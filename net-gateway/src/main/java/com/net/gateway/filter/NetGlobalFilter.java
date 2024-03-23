package com.net.gateway.filter;

import com.net.gateway.util.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class NetGlobalFilter implements GlobalFilter, Ordered {
    @Value("${exclude.path}")
    private String[] excludePath;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request=exchange.getRequest();
//        return chain.filter(exchange);
        if(isExclude(request.getPath().toString())){
            System.out.println("ok");
            return chain.filter(exchange);
        }
        List<String> list=request.getHeaders().get("authorization");
        String token=null;
        if(list!=null&&!list.isEmpty()){
            token=list.get(0);
        }
        String userId;
        System.out.println(token);
        try{
            userId= JWTUtil.parseJWT(token);
            System.out.println(userId);
        }
        catch (Exception e){
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        exchange.mutate()
                .request(builder -> builder.header("user-info",userId))
                .build();
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
    public boolean isExclude(String path){
        for(int i=0;i< excludePath.length;i++)
            if(path.startsWith(excludePath[i]))
                return true;
        return false;
    }
}
