package com.itcotato.naengjango.domain.member.entity;

import com.itcotato.naengjango.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class FixedExpenditure extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fixed_expenditure_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String item; // 항목명

    @Column(nullable = false)
    private Long amount; // 금액

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;
}
