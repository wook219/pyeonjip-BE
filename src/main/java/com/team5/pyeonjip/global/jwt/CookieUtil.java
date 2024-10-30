package com.team5.pyeonjip.global.jwt;

import jakarta.servlet.http.Cookie;

public class CookieUtil {

    String COOKIE_DOMAIN = "localhost";
    String COOKIE_PATH = "/";
    int COOKIE_MAX_AGE = 24 * 60 * 60;

    public Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);

        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setPath(COOKIE_PATH);
        cookie.setDomain(COOKIE_DOMAIN);
        cookie.setSecure(true);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
