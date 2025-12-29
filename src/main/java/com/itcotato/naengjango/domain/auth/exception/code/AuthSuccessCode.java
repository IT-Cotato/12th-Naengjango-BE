package com.itcotato.naengjango.domain.auth.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 인증 관련 성공 코드 정의
 */
@Getter
@AllArgsConstructor
public enum AuthSuccessCode implements BaseSuccessCode {

    // 로그인 관련 성공 코드
    LOGIN_SUCCESS(HttpStatus.OK,
            "LOGIN200_1",
            "로그인에 성공했습니다."),

    // 로그아웃 관련 성공 코드
    LOGOUT_SUCCESS(HttpStatus.OK,
            "LOGOUT200_1",
            "로그아웃에 성공했습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
