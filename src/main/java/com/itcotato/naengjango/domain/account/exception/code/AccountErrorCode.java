package com.itcotato.naengjango.domain.account.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 가계부 관련 에러 코드 정의
 */

@Getter
@AllArgsConstructor
public enum AccountErrorCode implements BaseErrorCode {
    // 남은 예산 정보 조회(오늘 남은 예산, 이달 남은 예산) 관련 에러 코드
    ACCOUNT_BAD_REQUEST(HttpStatus.BAD_REQUEST, "ACCOUNT400_1", "잘못된 요청입니다."),
    ACCOUNT_FORBIDDEN(HttpStatus.FORBIDDEN, "ACCOUNT403_1", "해당 예산 정보에 접근할 권한이 없습니다."),
    ACCOUNT_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ACCOUNT500_1", "예기치 않은 서버 에러가 발생했습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
