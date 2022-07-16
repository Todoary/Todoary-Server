package com.todoary.ms.util;

public class Secret {
    public static final long JWT_ACCESS_TOKEN_EXPTIME = 1000 * 60 * 10;
    public static final long JWT_REFRESH_TOKEN_EXPTIME = 1000 * 60 * 60 * 24;
    public static final String  JWT_ACCESS_SECRET_KEY = "access";
    public static final String  JWT_REFRESH_SECRET_KEY = "refresh";
}
