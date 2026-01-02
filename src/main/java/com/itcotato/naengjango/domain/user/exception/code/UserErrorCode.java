package com.itcotato.naengjango.domain.user.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 회원가입 관련 에러 코드 정의
 */

@Getter
@AllArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    // 아이디 중복 확인 관련 에러 코드
    USER_ID_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER400_1", "이미 존재하는 아이디입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
