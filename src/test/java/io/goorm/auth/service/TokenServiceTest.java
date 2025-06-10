package io.goorm.auth.service;

import io.goorm.auth.dao.RefreshTokenRepository;
import io.goorm.auth.domain.RefreshToken;
import io.goorm.auth.dto.response.AuthTokenResponse;
import io.goorm.config.exception.CustomException;
import io.goorm.config.security.JwtUtil;
import io.goorm.member.domain.Member;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class TokenServiceTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @PersistenceContext
    EntityManager em;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;


    @Test
    void refresh_성공() {
        // Given
        Member member = new Member("test", "testNickname", "testPassword");
        em.persist(member);

        // When
        String refreshToken = jwtUtil.generateRefreshToken(member.getId(), member.getRole());

        RefreshToken save = refreshTokenRepository.save(RefreshToken.createRefreshToken(refreshToken, member, jwtUtil.getExpiredAtByToken(refreshToken)));

        AuthTokenResponse response = tokenService.refresh(save.getValue());

        // Then
        assertNotNull(response);
        assertEquals(response.refreshToken(), save.getValue());
    }

    @Test
    void refresh_실패_만료된_토큰() {
        String token = "expired";
        // When & Then
        assertThrows(CustomException.class, () -> {
            tokenService.refresh(token);
        });
    }

    @Test
    void refresh_실패_만료_되지_않은_토큰_디비에_없음() {
        // Given
        Member member = new Member("test", "testNickname", "testPassword");
        em.persist(member);

        // When
        String refreshToken = jwtUtil.generateRefreshToken(member.getId(), member.getRole());
        // Then
        assertThrows(CustomException.class, () -> {
            tokenService.refresh(refreshToken);
        }).getErrorMessage().contains("AUTH_TOKEN_EXPIRED");
    }

    @Test
    void regenerateAndStoreTokens_성공() {
        // Given
        Member member = new Member("test", "testNickname", "testPassword");
        em.persist(member);

        // When
        AuthTokenResponse tokens = jwtUtil.generateTokens(member.getId(), member.getRole());
        RefreshToken response = refreshTokenRepository.save(RefreshToken.createRefreshToken(tokens.refreshToken(), member, jwtUtil.getExpiredAtByToken(tokens.refreshToken())));

        // Then
        assertNotNull(response);
        assertEquals(tokens.refreshToken(), response.getValue());
    }
}