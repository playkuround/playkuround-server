package com.playkuround.playkuroundserver.global.error.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    // Common
    ENTITY_NOT_FOUND(400, "C001", "엔티티 조회에 실패하였습니다."),
    INVALID_VALUE(400, "C002", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(405, "C003", "잘못된 HTTP 메서드입니다."),
    ACCESS_DENIED(403, "C004", "권한이 없습니다."),
    INTERNAL_SERVER_ERROR(500, "C005", "서버 내부에서 에러가 발생하였습니다."),

    // User
    EMAIL_DUPLICATION(400, "U001", "이미 존재하는 이메일입니다."),
    NICKNAME_DUPLICATION(400, "U002", "이미 존재하는 닉네임입니다."),

    // Authentication
    EMPTY_AUTHORIZATION(401, "A001", "Authorization Header가 빈 값입니다."),
    NOT_BEARER_GRANT_TYPE(401, "A002", "인증 타입이 Bearer 타입이 아닙니다."),
    INVALID_TOKEN(401, "A003", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "A004", "만료된 토큰입니다."),
    NOT_ACCESS_TOKEN_TYPE(401, "A005", "TokenType이 ACCESS가 아닙니다."),
    NOT_REFRESH_TOKEN_TYPE(401, "A006", "TokenType이 REFRESH가 아닙니다."),

    // Location
    INVALID_LOCATION_LANDMARK(401, "L001", "현재 위치와 랜드마크 위치가 너무 멉니다."),
    INVALID_LOCATION_KU(401, "L002", "건국대학교 내에 위치하고 있지 않습니다."),

    // Score
    INVALID_SCORE_TYPE(401, "S001", "올바르지 않은 ScoreType입니다."),

    // Email
    NOT_KU_EMAIL(400, "E001", "건국대학교 이메일이 아닙니다."),
    EMAIL_SEND_FAIL(500, "E002", "이메일 전송에 실패하였습니다."),
    EXPIRED_AUTH_CODE(400, "E003", "만료된 코드입니다."),

    ;

    private final int status;
    private final String code;
    private final String message;

}
