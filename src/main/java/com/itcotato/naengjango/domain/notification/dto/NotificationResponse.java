package com.itcotato.naengjango.domain.notification.dto;

import com.itcotato.naengjango.domain.favoriteapp.enums.SupportedApp;
import com.itcotato.naengjango.domain.notification.entity.Notification;
import com.itcotato.naengjango.domain.notification.entity.NotificationType;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record NotificationResponse(
        Long id,
        NotificationType type,
        String message,
        String link,
        boolean isRead,
        LocalDateTime createdAt,
        String appIconKey
) {
    public static NotificationResponse from(Notification n) {
        return new NotificationResponse(
                n.getId(),
                n.getType(),
                n.getMessage(),
                n.getLink(),
                n.isRead(),
                n.getCreatedAt(),
                n.getAppIconKey() == null ? "defaultImg" : n.getAppIconKey()
        );
    }
}
