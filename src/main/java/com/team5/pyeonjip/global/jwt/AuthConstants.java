package com.team5.pyeonjip.global.jwt;

public class AuthConstants {

    public static final String DOMAIN = "http://localhost:3000";

    // Access Token을 전달할 헤더
    public static final String AUTH_HEADER = "Authorization";

    // Access Token의 타입
    public static final String AUTH_TYPE = "Bearer ";

    // 각 Token의 유효 시간
    public static final Long REFRESH_TOKEN_EXPIRED_MS = 600000L;
    public static final Long ACCESS_TOKEN_EXPIRED_MS = 86400000L;


}
