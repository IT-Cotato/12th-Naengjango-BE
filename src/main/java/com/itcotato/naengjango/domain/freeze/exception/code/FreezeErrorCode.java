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
    FREEZE_INVALID_STATUS(
            HttpStatus.BAD_REQUEST,
            "FREEZE400_2",
            "유효하지 않은 냉동 상태입니다."
    ),
    FREEZE_INVALID_APPNAME(
            HttpStatus.BAD_REQUEST,
            "FREEZE400_3",
            "유효하지 않은 앱 이름입니다."
    ),
    FREEZE_INVALID_ITEM(
            HttpStatus.BAD_REQUEST,
            "FREEZE400_4",
            "유효하지 않은 아이템 이름입니다."
    ),
    FREEZE_INVALID_PRICE(
            HttpStatus.BAD_REQUEST,
            "FREEZE400_5",
            "유효하지 않은 가격입니다."
    ),
    FREEZE_INVALID_USER(
            HttpStatus.UNAUTHORIZED,
            "FREEZE401_1",
            "유효하지 않은 사용자입니다."
    ),
    FREEZE_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "FREEZE404_1",
            "냉동 정보를 찾을 수 없습니다."
    ),
    FREEZE_INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "FREEZE500_1",
            "예기치 않은 서버 에러가 발생했습니다."
    ),
    FREEZE_FORBIDDEN(
            HttpStatus.FORBIDDEN,
            "FREEZE403_1",
            "냉동 작업에 대한 권한이 없습니다."
            );
    private final HttpStatus status;
    private final String code;
    private final String message;
}
