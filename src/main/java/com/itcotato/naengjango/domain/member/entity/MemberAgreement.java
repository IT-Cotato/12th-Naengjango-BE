package com.itcotato.naengjango.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class MemberAgreement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long memberAgreementId;

    private Boolean isAgreed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agreement_id")
    private Agreement agreement;

    /**
     * 생성 메서드 (팩토리)
     */
    public static MemberAgreement create(Member member, Agreement agreement) {
        return MemberAgreement.builder()
                .member(member)
                .agreement(agreement)
                .isAgreed(false)
                .build();
    }

    /**
     * 동의 상태 변경
     */
    public void updateAgreement(boolean agreed) {
        this.isAgreed = agreed;
    }

    public void agree() {
        this.isAgreed = true;
    }

    public void withdraw() {
        this.isAgreed = false;
    }

    /**
     * 현재 동의 여부
     */
    public boolean agreed() {
        return Boolean.TRUE.equals(isAgreed);
    }
}
