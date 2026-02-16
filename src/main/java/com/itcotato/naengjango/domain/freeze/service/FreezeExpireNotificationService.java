package com.itcotato.naengjango.domain.freeze.service;

import com.itcotato.naengjango.domain.freeze.entity.FreezeItem;
import com.itcotato.naengjango.domain.member.service.SmsService;
import com.itcotato.naengjango.domain.notification.entity.NotificationType;
import com.itcotato.naengjango.domain.notification.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FreezeExpireNotificationService {

    private final ApplicationEventPublisher eventPublisher;
    private final SmsService smsService;

    public void notifyExpire(FreezeItem item) {
        var member = item.getMember();

        String message = buildMessage(item);
        String link = "/freezes"; // 프론트 이동 경로

        /* 앱 내 알림 (DB 저장) */
        eventPublisher.publishEvent(
                new NotificationEvent(
                        member.getId(),
                        NotificationType.FREEZE_EXPIRED,
                        message,
                        link
                )
        );

        /* 문자 알림 (CoolSMS 재사용) */
        if (member.getPhoneNumber() != null && !member.getPhoneNumber().isBlank()) {
            smsService.sendFreezeExpiredSms(
                    member.getPhoneNumber(),
                    message
            );
        }
    }

    private String buildMessage(FreezeItem item) {
        return String.format(
                "[품목] 냉동이 종료되었습니다. 냉동을 녹여보세요!",
                item.getAppName(),
                item.getItemName(),
                item.getPrice()
        );
    }
}