package com.ably.dibs_api.config.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
public enum ErrorCode {

    // Unauthorized
    INVALID_JWT(UNAUTHORIZED, "유효하지 않은 토큰"),
    JWT_TOKEN_NOT_FOUND(UNAUTHORIZED, "토큰을 찾을 수 없음"),
    TOKEN_EXPIRED(UNAUTHORIZED, "만료된 토큰"),
    NOT_AUTHENTICATED_USER(UNAUTHORIZED, "권한 없는 사용자"),

    // Bad request
    NOT_FOUND_ENTITY(BAD_REQUEST, "엔티티가 존재하지 않음"),
    EXIST_EMAIL(BAD_REQUEST, "이미 가입된 이메일"),
    PASSWORD_INCORRECT(BAD_REQUEST, "패스워드 불일치"),

    // Internal Server error
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류");

    private HttpStatus httpStatus;
    private String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
