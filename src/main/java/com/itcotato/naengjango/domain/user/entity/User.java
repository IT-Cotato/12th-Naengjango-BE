package com.itcotato.naengjango.domain.user.entity;

import com.itcotato.naengjango.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 255)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialType socialType; // 기본, 구글을 담는 Enum

    private Integer budget; // 한 달 예산

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserAgreement> userAgreements = new ArrayList<>();

    public enum SocialType {
        GENERAL, GOOGLE
    }
}


