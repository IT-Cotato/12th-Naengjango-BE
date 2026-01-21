package com.itcotato.naengjango.domain.notification.exception.code;

import com.itcotato.naengjango.global.apiPayload.code.BaseSuccessCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 알림 관련 성공 코드 정의
 */
@Getter
@AllArgsConstructor
public enum NotificationSuccessCode implements BaseSuccessCode {

    NOTIFICATION_LIST_FOUND(
            HttpStatus.OK,
            "NOTIFICATION200_1",
            "알림 목록 조회 성공"
    ),

    NOTIFICATION_UNREAD_COUNT_FOUND(
            HttpStatus.OK,
            "NOTIFICATION200_2",
            "읽지 않은 알림 개수 조회 성공"
    ),

    NOTIFICATION_MARK_READ_SUCCESS(
            HttpStatus.OK,
            "NOTIFICATION200_3",
            "알림 단건 읽음 처리 성공"
    ),

    NOTIFICATION_MARK_READ_ALL_SUCCESS(
            HttpStatus.OK,
            "NOTIFICATION200_4",
            "전체 알림 읽음 처리 성공"
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
