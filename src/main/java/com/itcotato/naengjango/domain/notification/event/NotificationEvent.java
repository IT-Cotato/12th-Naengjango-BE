package com.itcotato.naengjango.domain.notification.event;

import com.itcotato.naengjango.domain.notification.entity.NotificationType;

public record NotificationEvent(
        Long receiverId,
        NotificationType type,
        String message,
        String link,
        String appIconKey
) {}
