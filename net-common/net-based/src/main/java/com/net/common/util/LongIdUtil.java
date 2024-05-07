package com.net.common.util;

import com.baomidou.mybatisplus.core.incrementer.DefaultIdentifierGenerator;

public class LongIdUtil {
    private static final DefaultIdentifierGenerator LONG_ID_GENERATOR=new DefaultIdentifierGenerator();
    public static Long createLongId(){
        return Long.valueOf((long)(Math.random()*(2)+1)+"" + (long)(Math.random()*(1e18-1e17)+1e17));
    }
    public static Long createLongId(Object entity){
        return LONG_ID_GENERATOR.nextId(entity);
    }
}
