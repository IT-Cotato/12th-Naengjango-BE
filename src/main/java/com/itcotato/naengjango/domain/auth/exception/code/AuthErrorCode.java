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
    LOGIN_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "LOGIN500_1",
            "예기치 않은 서버 에러가 발생했습니다."),
    INVALID_LOGIN_TYPE(HttpStatus.BAD_REQUEST,
            "LOGIN400_1",
            "유효하지 않은 로그인 타입입니다."),
    LOGIN_UNSUPPORTED_SOCIAL_TYPE(HttpStatus.BAD_REQUEST,
            "LOGIN400_2",
            "지원하지 않는 소셜 로그인 타입입니다."),
    LOGIN_INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,
            "LOGIN401_1",
            "비밀번호가 일치하지 않습니다."),
    LOGIN_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,
            "LOGIN401_2",
            "유효하지 않은 리프레시 토큰입니다."),
    LOGIN_INVALID_SOCIAL_TOKEN(
            HttpStatus.UNAUTHORIZED,
            "AUTH401_3",
            "유효하지 않은 소셜 로그인 토큰입니다."
    ),

    // 로그아웃 관련 에러 코드
    LOGOUT_BAD_REQUEST(HttpStatus.BAD_REQUEST,
            "LOGOUT400_1",
            "잘못된 요청입니다."),
    LOGOUT_UNAUTHORIZED(HttpStatus.UNAUTHORIZED,
            "LOGOUT401_1",
            "인증에 실패했습니다."),
    LOGOUT_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "LOGOUT500_1",
            "예기치 않은 서버 에러가 발생했습니다."),
    LOGOUT_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,
            "LOGOUT401_2",
            "유효하지 않은 리프레시 토큰입니다."),

    // 토큰 재발급 관련 에러 코드
    REISSUE_INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED,
            "TOKEN401_1",
            "유효하지 않은 리프레시 토큰입니다."),
    REISSUE_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,
            "TOKEN500_1",
            "예기치 않은 서버 에러가 발생했습니다.")
    ;



    private final HttpStatus status;
    private final String code;
    private final String message;
}
