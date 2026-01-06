package com.itcotato.naengjango.domain.user.entity;

import com.itcotato.naengjango.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import jakarta.persistence.Id;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Agreement extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long agreementId;

    @Column(nullable = false, length = 50)
    private String name; // 이용 약관 동의, 개인정보 수집 및 이용 동의, SNS 알림 허용

    @Column(nullable = false)
    private Boolean isRequired; // 필수 여부

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content; // 본문 내용

    @Column(nullable = false, length = 10)
    private String version; // 버전
}
