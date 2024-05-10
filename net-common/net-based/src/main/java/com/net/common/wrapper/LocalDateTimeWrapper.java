package com.net.common.wrapper;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class LocalDateTimeWrapper {
    public static LocalDateTime now(){
        return LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
    }
}
