package io.goorm.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthSignInResponse (
        @Schema(description = "액세스 토큰")
        String accessToken,

        @Schema(description = "리프레시 토큰")
        String refreshToken
) {
    public static AuthSignInResponse of(String accessToken, String refreshToken) {
        return new AuthSignInResponse(accessToken, refreshToken);
    }
}
