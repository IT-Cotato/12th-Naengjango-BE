package com.itcotato.naengjango.domain.user.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import com.itcotato.naengjango.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 회원가입 관련 성공 코드 정의
 */

@Getter
@AllArgsConstructor
public enum UserSuccessCode implements BaseSuccessCode {
    // 아이디 중복 확인 관련 성공 코드
    USER_ID_AVAILABLE(HttpStatus.OK, "USER200_1", "사용 가능한 아이디입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
