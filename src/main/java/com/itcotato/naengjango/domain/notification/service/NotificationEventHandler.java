package com.itcotato.naengjango.domain.notification.service;

import com.itcotato.naengjango.domain.member.repository.MemberRepository;
import com.itcotato.naengjango.domain.notification.entity.Notification;
import com.itcotato.naengjango.domain.notification.event.NotificationEvent;
import com.itcotato.naengjango.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class NotificationEventHandler {

    private final MemberRepository memberRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    @EventListener
    public void handle(NotificationEvent event) {
        var receiver = memberRepository.findById(event.receiverId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        notificationRepository.save(Notification.builder()
                .receiver(receiver)
                .type(event.type())
                .message(event.message())
                .link(event.link())
                .appIconKey(event.appIconKey())
                .build()
        );
    }
}
