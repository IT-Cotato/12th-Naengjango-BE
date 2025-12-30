package com.itcotato.naengjango.domain.user.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * SMS 인증 관련 성공 코드 정의
 */

@Getter
@AllArgsConstructor
public enum SmsSuccessCode implements BaseSuccessCode {
    // SMS 전송 관련 성공 코드
    SMS_SEND_SUCCESS(HttpStatus.OK, "SMS200_1", "문자 발송에 성공했습니다."),

    // 인증번호 인증 관련 성공 코드
    SMS_VERIFY_SUCCESS(HttpStatus.OK, "SMS200_2", "인증에 성공했습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

}
