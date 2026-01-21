package com.itcotato.naengjango.domain.notification.entity;

import com.itcotato.naengjango.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver_id", nullable = false)
    private Member receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    @Column(nullable = false, length = 300)
    private String message;

    @Column(length = 500)
    private String link;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Builder
    private Notification(Member receiver, NotificationType type, String message, String link) {
        this.receiver = receiver;
        this.type = type;
        this.message = message;
        this.link = link;
    }

    public void markRead() {
        this.isRead = true;
    }
}
