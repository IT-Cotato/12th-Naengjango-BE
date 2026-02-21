package com.itcotato.naengjango.domain.igloo.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum IglooSuccessCode implements BaseSuccessCode {

    IGLOO_STATUS_SUCCESS(HttpStatus.OK, "IGLOO200_1", "이글루 상태 조회 성공"),
    IGLOO_UPGRADE_SUCCESS(HttpStatus.OK, "IGLOO200_2", "이글루 업그레이드 성공"),
    IGLOO_DOWNGRADE_SUCCESS(HttpStatus.OK, "IGLOO200_3","이글루 다운그레이드 성공"),
    IGLOO_PROTECT_SUCCESS(HttpStatus.OK, "IGLOO200_4", "이글루 하락 방어 성공");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
