package com.team5.pyeonjip.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GlobalException extends RuntimeException {

    ErrorCode errorCode;
}
