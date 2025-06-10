package io.goorm.auth.service;

import io.goorm.auth.dao.RefreshTokenRepository;
import io.goorm.auth.domain.RefreshToken;
import io.goorm.auth.dto.response.AuthSignInResponse;
import io.goorm.config.exception.CustomException;
import io.goorm.config.exception.ErrorCode;
import io.goorm.config.security.JwtUtil;
import io.goorm.member.dao.MemberRepository;
import io.goorm.member.domain.Member;
import io.goorm.member.dto.request.MemberSaveRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AuthServiceTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TokenService tokenService;

    @PersistenceContext
    EntityManager em;
    @Autowired
    private AuthService authService;

    // save
    @Test
    void 회원가입_성공() {
        // Given
        String username = "testUser";
        String nickname = "testNickname";
        String password = "testPassword";

        MemberSaveRequest request = new MemberSaveRequest(username, nickname, passwordEncoder.encode(password));

        // When
        AuthSignInResponse response = authService.save(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.tokens().accessToken());
        assertNotNull(response.tokens().refreshToken());
        assertEquals(username, response.member().username());
        assertEquals(nickname, response.member().nickname());
    }

    @Test
    void 회원가입_실패_이미_존재하는_회원() {
        // Given
        String username = "existingUser";
        String nickname = "existingNickname";
        String password = "existingPassword";

        Member existingMember = new Member(username, nickname, passwordEncoder.encode(password));
        em.persist(existingMember);

        MemberSaveRequest request = new MemberSaveRequest(username, nickname, passwordEncoder.encode(password));

        // When & Then
        assertThrows(CustomException.class, () -> {
            authService.save(request);
        });
    }

    // signIn
    @Test
    void 로그인_성공() {
        //  Given
        String rawPassword = "password111";
        String encoded = passwordEncoder.encode(rawPassword);

        Member member = new Member("testUser", "testnick", encoded);
        em.persist(member);

        // When
        AuthSignInResponse response = authService.signIn("testUser", rawPassword);

        // Then
        assertThat(response.tokens().accessToken()).isNotNull();
        assertThat(response.tokens().refreshToken()).isNotNull();
        assertThat(response.member().username()).isEqualTo("testUser");
        assertThat(response.member().nickname()).isEqualTo("testnick");
    }

    @Test
    void 로그인_실패_비밀번호_불일치() {
        // Given
       Member member = new Member("test", "testnick", passwordEncoder.encode("password111")); // 멤버 생성.
        em.persist(member); // 멤버 커밋.

        // When & Then
        assertThatThrownBy(() -> authService.signIn("test", "wrongPassword")) // 다른 비밀번호로 로그인 시도.
                .isInstanceOf(CustomException.class) // 커스텀 예외 발생 확인.
                .hasMessage(ErrorCode.AUTH_USERNAME_PASSWORD_INVALID.name());
    }

    @Test
    void 로그인_실패_존재하지_않는_회원() {
        assertThatThrownBy(() -> authService.signIn("notExistingUser", "password111"))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.AUTH_USERNAME_PASSWORD_INVALID.name());
    }

    // signOut
    @Test
    void 로그아웃_성공() {
        // Given
        String username = "testUser";
        String nickname = "testNickname";
        String password = "testPassword";

        Member member = new Member(username, nickname, passwordEncoder.encode(password));
        em.persist(member);

        AuthSignInResponse response = authService.signIn("testUser", password);

        // When
        authService.signOut(response.tokens().refreshToken());

        // Then
        Optional<RefreshToken> byValueAndExpiredAtIsAfter = refreshTokenRepository.findByValueAndExpiredAtIsAfter(response.tokens().refreshToken(), LocalDateTime.now());
        assertThat(byValueAndExpiredAtIsAfter).isEmpty(); // 로그아웃 후 리프레시 토큰이 DB에 존재하지 않아야 함
    }

    @Test
    void 로그아웃_실패_존재하지_않는_토큰() {
        // Given
        String username = "testUser";
        String nickname = "testNickname";
        String password = "testPassword";

        Member member = new Member(username, nickname, passwordEncoder.encode(password));
        em.persist(member);

        String refreshToken = jwtUtil.generateRefreshToken(member.getId(), member.getRole()); // 토큰을 생성하지만 DB에 저장하지 않음

        // When & Then
        assertThatThrownBy(() -> authService.signOut(refreshToken))
                .isInstanceOf(CustomException.class)
                .hasMessage("AUTH_TOKEN_EXPIRED");
    }

    // regenerateTokens
    @Test
    void 토큰_재생성_성공() {
        // Given
        String username = "testUser";
        String nickname = "testNickname";
        String password = "testPassword";

        Member member = new Member(username, nickname, passwordEncoder.encode(password));
        em.persist(member);

        // When
        AuthSignInResponse response = authService.regenerateTokens(member);

        // Then
        assertNotNull(response);
        assertNotNull(response.tokens().accessToken());
        assertNotNull(response.tokens().refreshToken());
        assertEquals(member.getUsername(), response.member().username());
        assertEquals(member.getNickname(), response.member().nickname());
    }
}