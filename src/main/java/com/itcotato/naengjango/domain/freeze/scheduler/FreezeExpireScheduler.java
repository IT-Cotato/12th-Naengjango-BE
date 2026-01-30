package com.itcotato.naengjango.domain.freeze.scheduler;

import com.itcotato.naengjango.domain.freeze.entity.FreezeItem;
import com.itcotato.naengjango.domain.freeze.enums.FreezeStatus;
import com.itcotato.naengjango.domain.freeze.repository.FreezeItemRepository;
import com.itcotato.naengjango.domain.freeze.service.FreezeExpireNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FreezeExpireScheduler {

    private final FreezeItemRepository freezeItemRepository;
    private final FreezeExpireNotificationService notificationService;

    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void notifyExpiredFreezes() {
        LocalDateTime now = LocalDateTime.now();

        List<FreezeItem> targets =
                freezeItemRepository
                        .findByExpiresAtBeforeAndNotifiedFalseAndStatus(
                                now,
                                FreezeStatus.FROZEN
                        );

        for (FreezeItem item : targets) {
            try {
                notificationService.notifyExpire(item);
                item.markNotified();
            } catch (Exception e) {
                log.error(
                        "Freeze expire notification failed. freezeId={}",
                        item.getId(),
                        e
                );
            }
        }
    }
}