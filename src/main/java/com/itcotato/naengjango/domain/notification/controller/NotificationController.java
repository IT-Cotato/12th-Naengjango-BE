package com.itcotato.naengjango.domain.notification.controller;

import com.itcotato.naengjango.domain.notification.dto.*;
import com.itcotato.naengjango.domain.notification.service.CurrentMemberService;
import com.itcotato.naengjango.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final CurrentMemberService currentMemberService;

    @GetMapping
    public Page<NotificationResponse> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Long memberId = currentMemberService.getCurrentMemberId();
        return notificationService.getMyNotifications(memberId, page, size);
    }

    @GetMapping("/unread-count")
    public UnreadCountResponse getUnreadCount() {
        Long memberId = currentMemberService.getCurrentMemberId();
        return new UnreadCountResponse(notificationService.getUnreadCount(memberId));
    }

    @PatchMapping("/{id}/read")
    public void markRead(@PathVariable Long id) {
        Long memberId = currentMemberService.getCurrentMemberId();
        notificationService.markRead(memberId, id);
    }

    @PatchMapping("/read-all")
    public void markReadAll() {
        Long memberId = currentMemberService.getCurrentMemberId();
        notificationService.markReadAll(memberId);
    }
}
