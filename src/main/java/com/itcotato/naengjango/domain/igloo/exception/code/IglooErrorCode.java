package com.itcotato.naengjango.domain.igloo.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum IglooErrorCode implements BaseErrorCode {

    IGLOO_LEVEL_MAX(HttpStatus.BAD_REQUEST,
            "IGLOO400_1",
            "이미 최대 레벨에 도달했습니다."),

    SNOWBALL_INSUFFICIENT(HttpStatus.BAD_REQUEST,
            "IGLOO400_2",
            "눈덩이가 부족합니다."),

    NOT_REACHED_THRESHOLD(HttpStatus.BAD_REQUEST,
            "IGLOO400_3",
            "냉동 실패 횟수가 5 미만입니다."),

    IGLOO_LEVEL_MIN(HttpStatus.BAD_REQUEST,
            "IGLOO400_4",
            "이미 최소 레벨 입니다."),

    INVALID_PROTECT_REQUEST(HttpStatus.BAD_REQUEST,
            "IGLOO400_5",
            "하락 방어 요청이 유효하지 않습니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
