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
            "회원 냉동이 성공적으로 완료되었습니다."),
    FREEZE_LIST_SUCCESS(HttpStatus.OK,
            "FREEZE200_2",
            "회원 냉동 목록 조회에 성공했습니다."),
    FREEZE_CANCEL_SUCCESS(HttpStatus.OK,
            "FREEZE200_3",
            "회원 냉동 취소가 성공적으로 완료되었습니다."),
    FREEZE_DETAIL_SUCCESS(HttpStatus.OK,
            "FREEZE200_4",
            "회원 냉동 상세 조회에 성공했습니다."
            ),
    FREEZE_PURCHASE_SUCCESS(HttpStatus.OK,
            "FREEZE200_5",
            "회원 냉동 상품 구매가 성공적으로 완료되었습니다."),
    FREEZE_DELETE_SUCCESS(HttpStatus.OK,
            "FREEZE200_6",
            "회원 냉동 삭제가 성공적으로 완료되었습니다."),
    FREEZE_EXTEND_SUCCESS(HttpStatus.OK,
            "FREEZE200_7",
            "회원 냉동 연장이 성공적으로 완료되었습니다."),
    FREEZE_SUCCESS_SUCCESS(HttpStatus.OK,
            "FREEZE200_8",
            "회원 냉동 성공 처리가 완료되었습니다."),
    FREEZE_FAIL_SUCCESS(HttpStatus.OK,
            "FREEZE200_9",
            "회원 냉동 실패 처리가 완료되었습니다."),
    FREEZE_UPDATE_SUCCESS(HttpStatus.OK,
            "FREEZE200_10",
            "회원 냉동 정보 수정이 성공적으로 완료되었습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
