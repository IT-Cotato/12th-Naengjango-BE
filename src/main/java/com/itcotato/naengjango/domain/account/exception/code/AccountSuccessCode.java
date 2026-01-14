package com.itcotato.naengjango.domain.account.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 가계부 관련 성공 코드 정의
 */
@Getter
@AllArgsConstructor
public enum AccountSuccessCode implements BaseSuccessCode {
    // 남은 예산 정보 조회(오늘 남은 예산, 이달 남은 예산) 관련 성공 코드
    ACCOUNT_STATUS_SUCCESS(HttpStatus.OK, "ACCOUNT200_1", "예산 정보 조회에 성공하였습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
