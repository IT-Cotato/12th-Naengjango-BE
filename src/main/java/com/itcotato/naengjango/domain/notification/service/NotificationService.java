package com.itcotato.naengjango.domain.notification.service;

import com.itcotato.naengjango.domain.notification.dto.NotificationResponse;
import com.itcotato.naengjango.domain.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Page<NotificationResponse> getMyNotifications(Long memberId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return notificationRepository.findByReceiver_Id(memberId, pageable)
                .map(NotificationResponse::from);
    }

    @Transactional
    public void markRead(Long memberId, Long notificationId) {
        var n = notificationRepository.findByIdAndReceiver_Id(notificationId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        n.markRead();
    }

    @Transactional
    public void markReadAll(Long memberId) {
        // 간단 구현: 전부 가져와서 처리 (알림이 엄청 많아지면 벌크 업데이트로 변경 권장)
        var list = notificationRepository.findByReceiver_Id(memberId, Pageable.unpaged()).getContent();
        for (var n : list) {
            if (!n.isRead()) {
                n.markRead();
            }
        }
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(Long memberId) {
        return notificationRepository.countByReceiver_IdAndIsReadFalse(memberId);
    }
}
