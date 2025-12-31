package com.itcotato.naengjango.domain.user.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * SMS 인증 관련 에러 코드 정의
 */

@Getter
@AllArgsConstructor
public enum SmsErrorCode implements BaseErrorCode {
    // SMS 전송 관련 에러 코드
    SMS_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SMS500_1", "문자 발송에 실패했습니다."),

    // SMS 인증번호 관련 에러 코드
    SMS_BAD_REQUEST(HttpStatus.BAD_REQUEST, "SMS400_1", "인증번호가 일치하지 않습니다."),
    SMS_VERIFY_EXPIRED(HttpStatus.BAD_REQUEST, "SMS400_2", "인증 시간이 만료되었습니다. 다시 시도해주세요.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
