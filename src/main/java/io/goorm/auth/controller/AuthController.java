package io.goorm.auth.controller;

import io.goorm.auth.dto.request.AuthRefreshTokenReIssueRequest;
import io.goorm.auth.dto.request.AuthSignInRequest;
import io.goorm.auth.dto.request.AuthSignOutRequest;
import io.goorm.auth.dto.response.AuthMemberResponse;
import io.goorm.auth.dto.response.AuthSignInResponse;
import io.goorm.auth.dto.response.AuthTokenResponse;
import io.goorm.auth.service.AuthService;
import io.goorm.auth.service.TokenService;
import io.goorm.config.cookie.CookieUtil;
import io.goorm.config.exception.CustomException;
import io.goorm.config.exception.ErrorCode;
import io.goorm.member.dto.request.MemberSaveRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "인증 관련 API")
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;
    private final TokenService tokenService;

    @PostMapping("/auth/sign-up")
    @Operation(summary = "회원가입", description = "회원가입 API")
    public ResponseEntity<AuthMemberResponse> signUp(@Valid @RequestBody MemberSaveRequest request) {

        AuthSignInResponse response = authService.save(request);

        String accessToken = response.tokens().accessToken();
        String refreshToken = response.tokens().refreshToken();

        HttpHeaders tokenHeaders = cookieUtil.generateTokenCookies(accessToken, refreshToken);

        return ResponseEntity.status(HttpStatus.CREATED).headers(tokenHeaders).body(response.member());
    }

    @PostMapping("/auth/sign-in")
    @Operation(summary = "로그인", description = "로그인 API")
    public ResponseEntity<AuthMemberResponse> signIn(@Valid @RequestBody AuthSignInRequest request) {
        AuthSignInResponse response = authService.signIn(request.username(), request.password());
        String accessToken = response.tokens().accessToken();
        String refreshToken = response.tokens().refreshToken();

        HttpHeaders tokenHeaders = cookieUtil.generateTokenCookies(accessToken, refreshToken);

        return ResponseEntity.status(HttpStatus.OK).headers(tokenHeaders).body(response.member());
    }

    //리프레쉬 토큰으로 재발급
    @PostMapping("/auth/reissue")
    @Operation(summary = "리프레쉬 토큰 재발급", description = "리프레쉬 토큰 재발급 API")
    @ApiResponse(responseCode = "200", description = "리프레쉬 토큰 재발급 성공")
    public ResponseEntity<Void> reissue(@CookieValue(name = "refresh_token", required = false) String cookieRefreshToken) {
        if (cookieRefreshToken == null) {
            throw new CustomException(ErrorCode.AUTH_REFRESH_TOKEN_MISSING_REQUEST);
        }

        AuthTokenResponse response = tokenService.refresh(cookieRefreshToken);

        String accessToken = response.accessToken();
        String refreshToken = response.refreshToken();

        HttpHeaders tokenHeaders = cookieUtil.generateTokenCookies(accessToken, refreshToken);

        return ResponseEntity.status(HttpStatus.OK).headers(tokenHeaders).body(null);
    }

    //로그아웃
    @PostMapping("/auth/sign-out")
    @Operation(summary = "로그아웃", description = "로그아웃 API")
    @ApiResponse(responseCode = "200", description = "로그아웃 성공")
    public ResponseEntity<Void> signOut(@CookieValue(name = "refresh_token", required = false) String cookieRefreshToken) {
        if (cookieRefreshToken != null) {
            authService.signOut(cookieRefreshToken);
        }

        HttpHeaders tokenHeaders = cookieUtil.deleteTokenCookies();

        return ResponseEntity.status(HttpStatus.OK).headers(tokenHeaders).body(null);
    }

}
