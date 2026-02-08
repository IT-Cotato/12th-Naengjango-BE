package com.itcotato.naengjango.domain.snowball.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SnowballErrorCode implements BaseErrorCode {

    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
