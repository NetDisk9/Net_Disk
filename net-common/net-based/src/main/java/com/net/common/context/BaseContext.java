package com.net.common.context;


import java.util.HashMap;
import java.util.Map;

public class BaseContext {
    // 使用 ThreadLocal 存储线程局部的 Map
    private static ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(HashMap::new);

    // 设置当前线程的 id
    public static void setCurrentId(Long id) {
        threadLocal.get().put("id", id);
    }
    // 获取当前线程的 id
    public static Long getCurrentId() {
        return (Long) threadLocal.get().get("id");
    }

    // 设置当前线程的 ip
    public static void setCurrentIp(String ip) {
        threadLocal.get().put("ip", ip);
    }

    // 获取当前线程的 ip
    public static String getCurrentIp() {
        return (String) threadLocal.get().get("ip");
    }

    // 移除当前线程的 id
    public static void removeCurrentId() {
        threadLocal.get().remove("id");
    }

    // 移除当前线程的 ip
    public static void removeCurrentIp() {
        threadLocal.get().remove("ip");
    }

    // 移除当前线程的所有数据
    public static void clear() {
        threadLocal.remove();
    }
}

