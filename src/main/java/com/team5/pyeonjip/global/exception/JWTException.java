package com.team5.pyeonjip.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@Getter
@AllArgsConstructor
public enum JWTException {
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", "UNAUTHORIZED"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "FORBIDDEN", "FORBIDDEN");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
