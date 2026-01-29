package com.itcotato.naengjango.domain.freeze.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FreezeSuccessCode implements BaseSuccessCode {

    FREEZE_CREATE_SUCCESS(HttpStatus.OK,
            "FREEZE200_1",
            "냉동이 성공적으로 완료되었습니다."),
    FREEZE_LIST_SUCCESS(HttpStatus.OK,
            "FREEZE200_2",
            "냉동 목록 조회에 성공했습니다."),
    FREEZE_PURCHASE_SUCCESS(HttpStatus.OK,
            "FREEZE200_3",
            "냉동 실패(냉동 상품 구매)가 성공적으로 완료되었습니다."),
    FREEZE_EXTEND_SUCCESS(HttpStatus.OK,
            "FREEZE200_4",
            "계속 냉동(냉동 연장)이 성공적으로 완료되었습니다."),
    FREEZE_SUCCESS_SUCCESS(HttpStatus.OK,
            "FREEZE200_5",
            "냉동 성공 처리가 완료되었습니다."),
    FREEZE_UPDATE_SUCCESS(HttpStatus.OK,
            "FREEZE200_6",
            "냉동 정보 수정이 성공적으로 완료되었습니다."),
    FREEZE_BUDGET_PREVIEW_SUCCESS(HttpStatus.OK,
            "FREEZE200_7",
            "냉동 예산 미리보기가 성공적으로 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
