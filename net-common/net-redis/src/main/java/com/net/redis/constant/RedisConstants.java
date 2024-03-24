package com.net.redis.constant;

public class RedisConstants {
    public static final String LOGIN_CODE_KEY = "login:code:";
    public static final Long LOGIN_CODE_TTL = 2L;

    public static final String LOGIN_USER_KEY = "login:token:";
    public static final Long LOGIN_USER_TTL = 3600000L;
    public static final String PASSWORD_ERROR_TIMES = "password:error:";
    public static final Long PASSWORD_ERROR_TTL = 3600L;

    public static final int MAX_ERROR_TIMES = 5;
}
