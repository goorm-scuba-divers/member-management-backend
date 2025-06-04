package io.goorm.config.cookie;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import static io.goorm.config.cookie.CookieConstants.*;

@Component
@RequiredArgsConstructor
public class CookieUtil {



    public HttpHeaders generateTokenCookies(String accessToken, String refreshToken) {

        String sameSite = getSamSitePolicy();

        ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, accessToken)
                .path("/")
                .secure(true)
                .httpOnly(true)
                .sameSite(sameSite)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
                .path("/")
                .secure(true)
                .httpOnly(true)
                .sameSite(sameSite)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    private static String getSamSitePolicy() {
        // 쿠키 전송 방식.
        return Cookie.SameSite.NONE.attributeValue();
    }

    public HttpHeaders deleteTokenCookies() {
       String sameSite = getSamSitePolicy();

        ResponseCookie accessTokenCookie = ResponseCookie.from(ACCESS_TOKEN_COOKIE_NAME, "")
                .path("/")
                .secure(true)
                .httpOnly(true)
                .sameSite(sameSite)
                .maxAge(0)
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from(REFRESH_TOKEN_COOKIE_NAME, "")
                .path("/")
                .secure(true)
                .httpOnly(true)
                .sameSite(sameSite)
                .maxAge(0)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }
}
