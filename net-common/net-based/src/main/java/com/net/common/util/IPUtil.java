package com.net.common.util;

import org.springframework.http.server.reactive.ServerHttpRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IPUtil {

    public static String getIp(HttpServletRequest request){
        List<String> ipHeadList = Stream.of("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "X-Real-IP").collect(Collectors.toList());
        for (String ipHead : ipHeadList) {
            if (checkIP(request.getHeader(ipHead))) {
                String temp=request.getHeader(ipHead).split(",")[0];
                if("0:0:0:0:0:0:0:1".equals(temp)){
                    temp="127.0.0.1";
                }
                return temp;
            }
        }
        return "0:0:0:0:0:0:0:1".equals(request.getRemoteAddr()) ? "127.0.0.1" : request.getRemoteAddr();
    }
    /**
     * 检查ip存在
     */
    private static boolean checkIP(String ip) {
        return !(null == ip || 0 == ip.length() || "unknown".equalsIgnoreCase(ip));
    }
    public static String getIp(ServerHttpRequest request) {
        List<String> ipHeadList = Stream.of("X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP", "HTTP_CLIENT_IP", "X-Real-IP").collect(Collectors.toList());
        for (String ipHead : ipHeadList) {
            String firstHeader = request.getHeaders().getFirst(ipHead);
            if (checkIP(firstHeader)) {
                String temp = firstHeader.split(",")[0];
                if ("0:0:0:0:0:0:0:1".equals(temp)) {
                    temp = "127.0.0.1";
                }
                return temp;
            }
        }
        String hostString = Objects.requireNonNull(request.getRemoteAddress()).getHostString();
        return "0:0:0:0:0:0:0:1".equals(hostString) ? "127.0.0.1" : hostString;
    }
}
