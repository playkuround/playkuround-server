package com.playkuround.playkuroundserver.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    NOT_FOUND(HttpStatus.BAD_REQUEST, "C001", "Not Found resource"),
    INVALID_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 입력값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C003", "잘못된 HTTP 메서드입니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C004", "권한이 없습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C005", "서버 내부에서 에러가 발생하였습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "C006", "Bad Request"),

    // User
    EMAIL_DUPLICATION(HttpStatus.BAD_REQUEST, "U001", "이미 존재하는 이메일입니다."),
    NICKNAME_DUPLICATION(HttpStatus.BAD_REQUEST, "U002", "이미 존재하는 닉네임입니다."),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "U003", "존재하지 않는 유저입니다."),

    // Authentication
    EMPTY_AUTHORIZATION(HttpStatus.UNAUTHORIZED, "A001", "Authorization Header가 빈 값입니다."),
    NOT_BEARER_GRANT_TYPE(HttpStatus.UNAUTHORIZED, "A002", "인증 타입이 Bearer 타입이 아닙니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "A003", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "A004", "만료된 토큰입니다."),
    NOT_ACCESS_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "A005", "TokenType이 ACCESS가 아닙니다."),
    NOT_REFRESH_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "A006", "TokenType이 REFRESH가 아닙니다."),

    // Location
    INVALID_LOCATION_LANDMARK(HttpStatus.BAD_REQUEST, "L001", "현재 위치와 랜드마크 위치가 너무 멉니다."),
    INVALID_LOCATION_KU(HttpStatus.BAD_REQUEST, "L002", "건국대학교 내에 위치하고 있지 않습니다."),

    // Score
    INVALID_SCORE_TYPE(HttpStatus.BAD_REQUEST, "S001", "올바르지 않은 ScoreType입니다."),

    // Email
    NOT_KU_EMAIL(HttpStatus.BAD_REQUEST, "E001", "건국대학교 이메일이 아닙니다."),
    EMAIL_SEND_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "E002", "이메일 전송에 실패하였습니다."),
    EXPIRED_AUTH_CODE(HttpStatus.BAD_REQUEST, "E003", "만료된 코드입니다."),
    SENDING_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "E004", "인증 메일 전송 횟수를 초과하였습니다."),
    NOT_MATCH_AUTH_CODE(HttpStatus.BAD_REQUEST, "E005", "코드가 일치하지 않습니다."),

    // Badge
    INVALID_Badge_TYPE(HttpStatus.BAD_REQUEST, "B001", "올바르지 않은 BadgeType입니다."),

    // Attendance
    DUPLICATE_ATTENDANCE(HttpStatus.BAD_REQUEST, "AT01", "이미 오늘 출석한 회원입니다."),

    // Adventure
    DUPLICATE_ADVENTURE(HttpStatus.BAD_REQUEST, "AD01", "이미 오늘 탐험한 랜드마크입니다."),

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

}
