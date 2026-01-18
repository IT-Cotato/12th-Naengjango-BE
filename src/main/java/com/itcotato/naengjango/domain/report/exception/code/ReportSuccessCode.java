package com.itcotato.naengjango.domain.report.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseSuccessCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 분석 리포트 관련 성공 코드 정의
 */

@Getter
@RequiredArgsConstructor
public enum ReportSuccessCode implements BaseSuccessCode {
    //  하루 가용 예산 추이 변화와 파산 시나리오 관련 성공 코드
    REPORT_GET_SUCCESS(HttpStatus.OK, "REPORT200_1", "분석 리포트 조회에 성공하였습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
