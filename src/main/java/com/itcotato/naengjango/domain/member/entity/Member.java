package com.itcotato.naengjango.domain.member.entity;

import com.itcotato.naengjango.domain.member.enums.Role;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import com.itcotato.naengjango.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseEntity {

    /** 회원 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    /**
     * 로그인 전용 ID
     * LOCAL 로그인에서만 사용
     * SOCIAL 로그인은 내부 식별용으로만 사용
     */
    @Column(name = "login_id", length = 50)
    private String loginId;

    /** 회원 이름 */
    @Column(nullable = false, length = 100)
    private String name;

    /** Local 로그인만 사용하는 비밀번호 */
    @Column(length = 255)
    private String password;

    /** 회원 전화번호 */
    @Column(name = "phone_number", length = 255)
    private String phoneNumber;

    /** 소셜 로그인 타입 */
    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false)
    private SocialType socialType;

    /** 소셜 로그인 고유 ID */
    @Column(name = "social_id", length = 255)
    private String socialId;

    /** 회원 예산 */
    @Column
    private Integer budget;

    /** 회원 권한 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    /** 회원의 현재 이글루 레벨 */
    @Builder.Default
    @Column(nullable = false)
    private int iglooLevel = 1;

    /** 회원의 냉동 실패 누적 횟수 */
    @Builder.Default
    @Column(nullable = false)
    private int freezeFailCount = 0;

    /** 계정 생성 시간 **/
    @Column(nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    // =========================
    // 추가: 회원 탈퇴(소프트 삭제)
    // =========================

    /**
     * 회원 탈퇴 처리 시각
     * - null: 활성 회원
     * - not null: 탈퇴(비활성) 회원
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /** 회원 생성 시 기본 권한 설정 */
    @PrePersist
    private void setDefaultRole() {
        if (this.role == null) this.role = Role.USER;
        if (this.iglooLevel == 0) this.iglooLevel = 1;
    }

    /**
     * 로컬 계정 여부
     */
    public boolean isLocalAccount() {
        return this.socialType == SocialType.LOCAL;
    }

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberAgreement> memberAgreements = new ArrayList<>();

    /**
     * 소셜 로그인 계정 여부
     */
    public boolean isSocialAccount() {
        return this.socialType != SocialType.LOCAL;
    }

    public Long getMemberId() {
        return this.id;
    }

    public void updateBudget(Integer budget) {
        this.budget = budget;
    }

    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // 냉동 실패 누적 관련 메서드
    public void addFreezeFailCount(int delta) {
        if (delta <= 0) return;
        this.freezeFailCount += delta;
    }

    public boolean reachedFailThreshold(int threshold) {
        return this.freezeFailCount >= threshold;
    }

    public void resetFreezeFailCount() {
        this.freezeFailCount = 0;
    }

    public void downIglooLevel() {
        if (this.iglooLevel > 1) this.iglooLevel -= 1;
    }

    public void upIglooLevel() {
        if (this.iglooLevel < 5) this.iglooLevel += 1;
    }

    public boolean isMaxIglooLevel() {
        return this.iglooLevel >= 5;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    // =========================
    // 추가: 탈퇴 관련 메서드
    // =========================

    /**
     * 탈퇴 여부
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    /**
     * 회원 탈퇴 처리(소프트 삭제)
     * - deletedAt 세팅
     * - withdrawReason 저장(옵션)
     * - 개인정보 익명화(마스킹)
     */
    public void withdraw() {
        if (this.deletedAt != null) return; // 멱등 처리(이미 탈퇴면 다시 처리하지 않음)

        this.deletedAt = LocalDateTime.now();
        anonymizePersonalInfo();
    }

    /**
     * 개인정보 익명화
     *
     * 주의:
     * - phoneNumber는 nullable=false 이므로 null로 두지 말고 마스킹 문자열로 대체
     * - loginId가 unique 제약이 있다면, 충돌 방지를 위해 "deleted_{id}_{timestamp}" 등으로 변경 권장
     */
    private void anonymizePersonalInfo() {
        // 이름 마스킹
        this.name = "탈퇴회원";

        // loginId 마스킹(LOCAL만 의미 있지만, unique 충돌 방지 목적)
        // loginId가 null일 수 있으므로 방어
        if (this.loginId != null) {
            this.loginId = "deleted_" + this.id + "_" + System.currentTimeMillis();
        }

        // 전화번호 마스킹 (nullable=false)
        this.phoneNumber = "deleted";

        // 비밀번호 무력화 (LOCAL 계정이면 더 의미 있음)
        this.password = "withdrawn";

        // 소셜 식별자 제거(선택) - 필요시 null 처리
        this.socialId = null;

        // 예산 등 서비스성 데이터는 유지/삭제 정책에 따라 선택
        // this.budget = null; // 원하면 이렇게
    }
}
