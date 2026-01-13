package com.itcotato.naengjango.member.entity;

import com.itcotato.naengjango.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "loginid")
    private String loginId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "phone_number", nullable = false, length = 25)
    private String phoneNumber;

    @Column(nullable = false)
    private Integer budget;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_type", nullable = false, length = 20)
    private SocialType socialType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Column(name = "social_id")
    private String socialId;

    public void updateBudget(Integer budget) {
        this.budget = budget;
    }
}
