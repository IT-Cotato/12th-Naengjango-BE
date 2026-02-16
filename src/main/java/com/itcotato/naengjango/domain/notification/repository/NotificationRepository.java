package com.itcotato.naengjango.domain.notification.repository;

import com.itcotato.naengjango.domain.notification.entity.Notification;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByReceiver_Id(Long receiverId, Pageable pageable);

    Optional<Notification> findByIdAndReceiver_Id(Long id, Long receiverId);

    long countByReceiver_IdAndIsReadFalse(Long receiverId);

}
