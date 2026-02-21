package com.itcotato.naengjango.domain.snowball.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SnowballSuccessCode implements BaseSuccessCode {

    SNOWBALL_SUMMARY_SUCCESS(HttpStatus.OK,
            "SNOWBALL200_1",
            "눈덩이 요약 정보 조회에 성공했습니다."),
    SNOWBALL_HISTORY_SUCCESS(HttpStatus.OK,
            "SNOWBALL200_2",
            "눈덩이 히스토리 조회에 성공했습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
