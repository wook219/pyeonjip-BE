package com.team5.pyeonjip.global.jwt;

public class AuthConstants {

    // Access Token을 전달할 헤더
    private final String AUTH_HEADER = "Authorization";

    // Access Token의 타입
    private final String AUTH_TOKEN = "Bearer ";

    // 각 Token의 유효 시간
    private final Long REFRESH_TOKEN_EXPIRED_MS = 600000L;
    private final Long ACCESS_TOKEN_EXPIRED_MS = 86400000L;


}
