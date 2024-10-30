package com.team5.pyeonjip.global.jwt;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    public final static String COOKIE_DOMAIN = "https://ehedrefxzmygttpe.tunnel-pt.elice.io";
    public final static String COOKIE_PATH = "/";
    public final static int COOKIE_MAX_AGE = 24 * 60 * 60;


    // Todo: 정적 메서드로 관리해도 괜찮은가?
    public static Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);

        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath(COOKIE_PATH);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
