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
    // 예산 정보 조회(오늘 남은 예산, 이달 남은 예산, 날짜별 예산) 관련 성공 코드
    ACCOUNT_STATUS_SUCCESS(HttpStatus.OK, "ACCOUNT200_1", "예산 정보 조회에 성공하였습니다."),

    // 문자 파싱 관련 성공 코드
    PARSE_SUCCESS(HttpStatus.OK, "PARSE200_1", "문자 파싱에 성공했습니다."),

    // 가계부 내역 저장 관련 성공 코드
    TRANSACTION_SAVE_SUCCESS(HttpStatus.OK, "TRANSACTION200_1", "내역이 저장되었습니다."),

    // 가계부 내역 수정 관련 성공 코드
    TRANSACTION_UPDATE_SUCCESS(HttpStatus.OK, "TRANSACTION200_2", "내역이 수정되었습니다."),

    // 가계부 내역 삭제 관련 성공 코드
    TRANSACTION_DELETE_SUCCESS(HttpStatus.OK, "TRANSACTION200_3", "내역이 삭제되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
