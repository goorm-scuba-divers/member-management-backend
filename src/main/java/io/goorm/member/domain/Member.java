package io.goorm.member.domain;

import io.goorm.auth.domain.RefreshToken;
import jakarta.persistence.*;

@Entity
public class Member extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    protected Member() {}

    public Member(String username, String nickname, String password) {
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.role = MemberRole.USER;
    }

    public String getNickname() {
        return nickname;
    }

    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    public MemberRole getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public boolean isDeleted() {
        return this.getDeletedAt() != null;
    }
}
