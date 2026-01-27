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
       생성 로직
       ========================= */

    public static FreezeItem create(Member member,
                                    String appName,
                                    String itemName,
                                    int price,
                                    LocalDateTime deadline) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime resolvedDeadline = (deadline != null) ? deadline : now.plusHours(24);

        return FreezeItem.builder()
                .member(member)
                .appName(appName)
                .itemName(itemName)
                .price(price)
                .frozenAt(now)
                .deadline(resolvedDeadline)
                .status(FreezeStatus.FROZEN)
                .build();
    }

    /* =========================
       비즈니스 로직
       ========================= */

    /** 구매 확정: AVAILABLE 상태에서만 가능 */
    public void purchase() {
        if (this.status != FreezeStatus.AVAILABLE) {
            throw new IllegalStateException("구매 확정은 AVAILABLE 상태에서만 가능합니다.");
        }
        this.status = FreezeStatus.PURCHASED;
    }

    /** 구매 취소: PURCHASED면 취소 불가 (정책이 다르면 바꿔도 됨) */
    public void cancel() {
        if (this.status == FreezeStatus.PURCHASED) {
            throw new IllegalStateException("이미 구매된 항목은 취소할 수 없습니다.");
        }
        this.status = FreezeStatus.CANCELLED;
    }

    /** 냉동 해제 가능: FROZEN 상태에서만 가능 */
    public void makeAvailable() {
        if (this.status != FreezeStatus.FROZEN) {
            throw new IllegalStateException("FROZEN 상태에서만 AVAILABLE로 변경할 수 있습니다.");
        }
        this.status = FreezeStatus.AVAILABLE;
    }

    /** 마감시간이 지났는지 */
    public boolean isDeadlinePassed(LocalDateTime now) {
        return this.deadline != null && this.deadline.isBefore(now);
    }

    /** 정보 수정 */
    public void update(String appName, String itemName, Integer price) {
        if (appName != null) this.appName = appName;
        if (itemName != null) this.itemName = itemName;
        if (price != null) this.price = price;
    }

    public void extendDeadline(int hours) {
        this.deadline = this.deadline.plusHours(hours);
    }
}
