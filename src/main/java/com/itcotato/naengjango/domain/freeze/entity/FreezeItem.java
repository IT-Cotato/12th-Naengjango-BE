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
    private Long price;

    /**
     * 냉동 상태
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FreezeStatus status;

    /**
     * 냉동 시작 시간
     */
    @Column(name = "frozen_at", nullable = false)
    private LocalDateTime frozenAt;

    /**
     * 냉동 종료 시간
     * nullable (기본: frozenAt + 24시간)
     */
    @Column
    private LocalDateTime expiresAt;

    /**
     * 상태 확정 시각, Frozen 이면 null
     */
    private LocalDateTime decidedAt;

    /**
     * 알림 전송 여부
     */
    private boolean notified;

    /** 생성 메서드 */
    public static FreezeItem create(Member member, String appName, String itemName, Long price) {
        LocalDateTime now = LocalDateTime.now();
        FreezeItem item = new FreezeItem();
        item.member = member;
        item.appName = appName;
        item.itemName = itemName;
        item.price = price;
        item.status = FreezeStatus.FROZEN;
        item.frozenAt = now;
        item.expiresAt = now.plusHours(24);
        item.notified = false;
        item.decidedAt = null;
        return item;
    }

    /** 냉동 항목 수정 */
    public void update(String appName, String itemName, Long price) {
        this.appName = appName;
        this.itemName = itemName;
        this.price = price;
    }

    /** 타이머 24H로 재설정" */
    public void extend24Hours() {
        LocalDateTime now = LocalDateTime.now();
        this.frozenAt = now;
        this.expiresAt = now.plusHours(24);
        this.notified = false;
    }

    /** 상태 변경 메서드 - 성공 상태로 변경 */
    public void markSuccess() {
        this.status = FreezeStatus.SUCCESS;
        this.decidedAt = LocalDateTime.now();
    }

    /** 상태 변경 메서드 - 실패 상태로 변경 */
    public void markFailed() {
        this.status = FreezeStatus.FAILED;
        this.decidedAt = LocalDateTime.now();
    }

    /** 알림 전송 여부 설정 */
    public void markNotified() {
        this.notified = true;
    }

    /** 냉동 상태 확인 */
    public boolean isFrozen() {
        return this.status == FreezeStatus.FROZEN;
    }
}
