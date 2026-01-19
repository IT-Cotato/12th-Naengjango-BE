package com.itcotato.naengjango.domain.report.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 분석 리포트 관련 에러 코드 정의
 */

@Getter
@RequiredArgsConstructor
public enum ReportErrorCode implements BaseErrorCode {
    //  하루 가용 예산 추이 변화와 파산 시나리오 관련 에러 코드
    BUDGET_NOT_FOUND(HttpStatus.NOT_FOUND, "REPORT404_1", "설정된 예산 정보가 없습니다. 한 달 예산을 설정해주세요."),
    //  냉동 절약 효과 관련 에러 코드
    INVALID_PERIOD(HttpStatus.BAD_REQUEST, "REPORT400_1", "잘못된 기간 설정입니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
