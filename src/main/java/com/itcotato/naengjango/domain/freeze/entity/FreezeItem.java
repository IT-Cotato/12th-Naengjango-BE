package com.itcotato.naengjango.domain.freeze.entity;

import com.itcotato.naengjango.domain.freeze.enums.FreezeStatus;
import com.itcotato.naengjango.domain.member.entity.Member;
import com.itcotato.naengjango.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "freeze_item")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class FreezeItem extends BaseEntity {

    /**
     * 냉동 항목 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "freeze_item_id")
    private Long id;

    /**
     * 사용자 (N : 1)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    /**
     * 구매 예정 앱 이름
     * ex) 쿠팡, 무신사, 네이버쇼핑
     */
    @Column(name = "app_name", nullable = false, length = 64)
    private String appName;

    /**
     * 품목 이름
     */
    @Column(name = "item_name", nullable = false, length = 64)
    private String itemName;

    /**
     * 가격
     */
    @Column(nullable = false)
    private int price;

    /**
     * 냉동 종료 시간
     * nullable (기본: frozenAt + 24시간)
     */
    @Column
    private LocalDateTime deadline;

    /**
     * 냉동 시작 시간
     */
    @Column(name = "frozen_at", nullable = false)
    private LocalDateTime frozenAt;

    /**
     * 냉동 상태
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FreezeStatus status;

    /* =========================
       비즈니스 로직
       ========================= */

    /**
     * 구매 확정
     */
    public void purchase() {
        this.status = FreezeStatus.PURCHASED;
    }

    /**
     * 구매 취소
     */
    public void cancel() {
        this.status = FreezeStatus.CANCELLED;
    }

    /**
     * 냉동 해제 가능 상태로 변경
     */
    public void makeAvailable() {
        this.status = FreezeStatus.AVAILABLE;
    }
}
