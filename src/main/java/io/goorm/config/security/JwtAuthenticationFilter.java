package io.goorm.config.security;

import io.goorm.auth.dto.response.AuthTokenResponse;
import io.goorm.auth.service.AuthService;
import io.goorm.config.cookie.CookieUtil;
import io.goorm.config.dto.PrincipalDetails;
import io.goorm.config.dto.TokenDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;
import java.util.Objects;

import static io.goorm.config.cookie.CookieConstants.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // JWT 토큰을 요청 헤더에서 가져오기
        String accessTokenByHeader = getAccessTokenByHeader(request);
        String accessTokenByCookie = getAccessTokenByCookie(request);
        String refreshTokenByCookie = getRefreshTokenByCookie(request);

        String accessToken = accessTokenByHeader != null ? accessTokenByHeader : accessTokenByCookie;

        // 액세스 토큰이 존재할때.
        if (accessToken != null) {
            setAuthentication(accessToken);
            filterChain.doFilter(request, response);

            return;
        }

        if (refreshTokenByCookie != null) {
            AuthTokenResponse authTokenResponse = jwtUtil.refresh(refreshTokenByCookie);

            HttpHeaders httpHeaders = cookieUtil.generateTokenCookies(authTokenResponse.accessToken(), authTokenResponse.refreshToken());

            Objects.requireNonNull(httpHeaders.get(HttpHeaders.SET_COOKIE)).forEach(cookie -> {
                response.addHeader(HttpHeaders.SET_COOKIE, cookie);
            });

            setAuthentication(authTokenResponse.accessToken());
        }
        filterChain.doFilter(request, response);
    }

    private void setAuthentication(String token) {
        TokenDto tokenDto = jwtUtil.parseToken(token);

        if (tokenDto != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = new PrincipalDetails(tokenDto.memberId(), tokenDto.memberRole());
            Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
    }


    private String getAccessTokenByHeader(HttpServletRequest request) {
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            return accessToken.substring(7);
        }
        return null;
    }

    private String getAccessTokenByCookie(HttpServletRequest request) {
        Cookie accessTokenCookie = WebUtils.getCookie(request, ACCESS_TOKEN_COOKIE_NAME);
        if (accessTokenCookie != null) {
            return accessTokenCookie.getValue();
        }
        return null;
    }

    private String getRefreshTokenByCookie(HttpServletRequest request) {
        Cookie refreshTokenCookie = WebUtils.getCookie(request, REFRESH_TOKEN_COOKIE_NAME);
        if (refreshTokenCookie != null) {
            return refreshTokenCookie.getValue();
        }
        return null;
    }
}
