package com.itcotato.naengjango.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "favorite_app",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "app_name"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FavoriteApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "app_name", nullable = false, length = 30)
    private String appName;

    public FavoriteApp(Member member, String appName) {
        this.member = member;
        this.appName = appName;
    }
}