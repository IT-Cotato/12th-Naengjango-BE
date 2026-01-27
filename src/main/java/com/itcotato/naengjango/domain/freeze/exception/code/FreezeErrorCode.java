package com.itcotato.naengjango.domain.freeze.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FreezeErrorCode implements BaseErrorCode {

    FREEZE_INVALID_REQUEST(
            HttpStatus.BAD_REQUEST,
            "FREEZE400_1",
            "잘못된 냉동 요청입니다."
    ),
    FREEZE_ITEM_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "FREEZE404_1",
            "냉동 아이템을 찾을 수 없습니다."
    ),
    FREEZE_FORBIDDEN(
            HttpStatus.FORBIDDEN,
            "FREEZE403_1",
            "냉동 아이템에 대한 접근이 금지되었습니다."
    ),
    INVALID_STATUS_TRANSITION(
            HttpStatus.BAD_REQUEST,
            "FREEZE400_2",
            "유효하지 않은 상태 전환입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
