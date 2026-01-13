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
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
