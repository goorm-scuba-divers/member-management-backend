package io.goorm.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthSignInResponse (
        @Schema(description = "토큰 정보")
        AuthTokenResponse tokens,

        @Schema(description = "회원 정보")
        AuthMemberResponse member
) {
    public static AuthSignInResponse of(AuthTokenResponse tokenInfo, AuthMemberResponse memberInfo) {
        return new AuthSignInResponse(tokenInfo, memberInfo);
    }
}
