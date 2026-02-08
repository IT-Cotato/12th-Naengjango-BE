package com.itcotato.naengjango.domain.snowball.entity;

import com.itcotato.naengjango.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "snowball_ledger")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SnowballLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /** +1, +2, -1 */
    @Column(nullable = false)
    private int amount;

    /** FREEZE_SUCCESS, FREEZE_STREAK_3 ... */
    @Column(nullable = false, length = 30)
    private String reason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public static SnowballLedger earn(Member member, int amount, String reason) {
        SnowballLedger l = new SnowballLedger();
        l.member = member;
        l.amount = amount;
        l.reason = reason;
        l.createdAt = LocalDateTime.now();
        return l;
    }

    public static SnowballLedger spend(Member member, int amount, String reason) {
        SnowballLedger l = new SnowballLedger();
        l.member = member;
        l.amount = -amount;
        l.reason = reason;
        l.createdAt = LocalDateTime.now();
        return l;
    }
}