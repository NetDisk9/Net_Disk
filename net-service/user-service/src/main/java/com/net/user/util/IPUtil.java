package com.net.user.util;

import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IPUtil {
    private static Searcher searcher;

    public static String getIpAddress(String ip){
        if ("127.0.0.1".equals(ip) || ip.startsWith("192.168")||"0:0:0:0:0:0:0:1".equals(ip)) {
            return "局域网 ip";
        }
        if (searcher == null) {
            try {
                File file = ResourceUtils.getFile("classpath:ipdb/ip2region.xdb");
                String dbPath = file.getPath();
                searcher = Searcher.newWithFileOnly(dbPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String region = null;
        String errorMessage = null;
        try {
            region = searcher.search(ip);
        } catch (Exception e) {
            errorMessage = e.getMessage();
            if (errorMessage != null && errorMessage.length() > 256) {
                errorMessage = errorMessage.substring(0,256);
            }
            e.printStackTrace();
        }
        // 输出 region
        return region;
    }

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
}
