package com.itcotato.naengjango.domain.igloo.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum IglooErrorCode implements BaseErrorCode {

    IGLOO_LEVEL_MAX(HttpStatus.BAD_REQUEST, "IGLOO400_1", "이미 최대 레벨에 도달했습니다."),
    SNOWBALL_INSUFFICIENT(HttpStatus.BAD_REQUEST, "IGLOO400_2", "눈덩이가 부족합니다.");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
