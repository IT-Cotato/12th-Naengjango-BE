package com.itcotato.naengjango.domain.auth.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 인증 관련 에러 코드 정의
 */
@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    // 로그인 관련 에러 코드
    LOGIN_BAD_REQUEST(HttpStatus.BAD_REQUEST,
            "LOGIN400_1",
            "잘못된 요청입니다."),
    LOGIN_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,
            "LOGIN401_1",
            "인증에 실패했습니다."),
    LOGIN_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "LOGIN500_1",
            "예기치 않은 서버 에러가 발생했습니다."),

    // 로그아웃 관련 에러 코드
    LOGOUT_BAD_REQUEST(HttpStatus.BAD_REQUEST,
            "LOGOUT400_1",
            "잘못된 요청입니다."),
    LOGOUT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,
            "LOGOUT401_1",
            "인증에 실패했습니다."),
    LOGOUT_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "LOGOUT500_1",
            "예기치 않은 서버 에러가 발생했습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
