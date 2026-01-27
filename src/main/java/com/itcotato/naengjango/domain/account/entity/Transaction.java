package com.itcotato.naengjango.domain.account.entity;

import com.itcotato.naengjango.domain.account.enums.PaymentMethod;
import com.itcotato.naengjango.domain.account.enums.TransactionType;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Transaction extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // INCOME, EXPENSE

    @Column(nullable = false)
    private Long amount; // BIGINT

    @Column(nullable = false)
    private LocalDateTime date; // DATETIME

    @Column(nullable = false, length = 255)
    private String description; // 내역

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod payment; // CARD, CASH

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(length = 256)
    private String category;
}
