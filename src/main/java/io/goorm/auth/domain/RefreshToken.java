package io.goorm.auth.domain;

import io.goorm.member.domain.BaseTimeEntity;
import io.goorm.member.domain.Member;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Entity
public class RefreshToken extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "refresh_token", nullable = false, unique = true)
    private String value;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    protected RefreshToken() {}

    private RefreshToken(String value, Member member, Date expiredAt) {
        this.value = value;
        this.member = member;
        this.expiredAt = expiredAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static RefreshToken createRefreshToken(String value, Member member, Date expiredAt) {
        return new RefreshToken(value, member, expiredAt);
    }

    public void rotate(String refreshToken, Date expiredAt) {
        this.value = refreshToken;
        this.expiredAt = expiredAt.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public Member getMember() {
        return this.member;
    }

    public String getValue() {
        return this.value;
    }

}
