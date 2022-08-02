package com.playkuround.playkuroundserver.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Common
    ENTITY_NOT_FOUND(400, "C001", "엔티티 조회에 실패하였습니다."),
    INVALID_VALUE(400, "C002", "잘못된 입력값입니다."),

    // User
    EMAIL_DUPLICATION(400, "U001", "이미 존재하는 이메일입니다.")

    ;

    private final int status;
    private final String code;
    private final String message;

}
