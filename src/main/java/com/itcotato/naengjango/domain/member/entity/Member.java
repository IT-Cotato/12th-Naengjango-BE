package com.itcotato.naengjango.domain.member.entity;

import com.itcotato.naengjango.domain.member.enums.Role;
import com.itcotato.naengjango.domain.member.enums.SocialType;
import com.itcotato.naengjango.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "member")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
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
    @Column(name = "phone_number", nullable = false, length = 255)
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

    /** 회원 생성 시 기본 권한 설정 */
    @PrePersist
    private void setDefaultRole() {
        if (this.role == null) {
            this.role = Role.USER;
        }
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

}
