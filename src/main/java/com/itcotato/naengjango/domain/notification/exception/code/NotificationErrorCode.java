package com.itcotato.naengjango.domain.notification.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 알림 관련 에러 코드 정의
 */
@Getter
@AllArgsConstructor
public enum NotificationErrorCode implements BaseErrorCode {

    NOTIFICATION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "NOTIFICATION404_1",
            "알림을 찾을 수 없습니다."
    ),

    NOTIFICATION_ACCESS_DENIED(
            HttpStatus.BAD_REQUEST,
            "NOTIFICATION400_1",
            "해당 알림에 접근할 수 없습니다."
    ),

    INVALID_NOTIFICATION_TYPE(
            HttpStatus.BAD_REQUEST,
            "NOTIFICATION400_2",
            "지원하지 않는 알림 타입입니다."
    ),

    NOTIFICATION_INTERNAL_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "NOTIFICATION500_1",
            "알림 처리 중 오류가 발생했습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
