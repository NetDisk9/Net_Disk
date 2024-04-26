package com.net.common.util;

public class LongIdUtil {
    public static Long createLongId(){

        return Long.valueOf((long)(Math.random()*(2)+1)+"" + (long)(Math.random()*(1e18-1e17)+1e17));
    }
}
