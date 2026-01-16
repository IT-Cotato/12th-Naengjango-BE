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
    ACCOUNT_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "ACCOUNT500_1", "예기치 않은 서버 에러가 발생했습니다."),

    // 문자 내역 파싱 관련 에러 코드
    PARSE_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "PARSE400_1", "분석할 수 없는 문자열입니다."),
    PARSE_MISSING_INFO(HttpStatus.BAD_REQUEST, "PARSE400_3", "필수 정보(금액/날짜)를 추출할 수 없습니다."),
    PARSE_FAILED(HttpStatus.BAD_REQUEST, "PARSE400_4", "문자열 파싱 중 오류가 발생했습니다."),

    // 가계부 내역 저장 관련 에러 코드
    INVALID_TRANSACTION_AMOUNT(HttpStatus.BAD_REQUEST, "TRANSACTION400_1", "금액은 0원보다 커야 합니다."),
    INVALID_TRANSACTION_DATE(HttpStatus.BAD_REQUEST, "TRANSACTION400_2", "날짜 형식이 올바르지 않습니다. (yyyy-MM-dd)"),
    MISSING_TRANSACTION_REQUIRED(HttpStatus.BAD_REQUEST, "TRANSACTION400_3", "필수 입력 항목(내역/카테고리)이 누락되었습니다."),
    TRANSACTION_SAVE_FAILED(HttpStatus.BAD_REQUEST, "TRANSACTION400_4", "내역 저장 중 오류가 발생했습니다."),

    // 가계부 내역 조회 관련 에러 코드
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, "TRANSACTION400_5", "조회하는 날짜 형식이 올바르지 않습니다. (yyyy-MM-dd)"),

    // 가계부 내역 수정 관련 에러 코드
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "TRANSACTION404_1", "수정하려는 내역을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
