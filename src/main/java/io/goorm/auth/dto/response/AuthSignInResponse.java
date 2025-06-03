package io.goorm.auth.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthSignInResponse (
        @Schema(description = "토큰 정보")
        AuthTokenInfo tokens,

        @Schema(description = "회원 정보")
        AuthMemberInfo member
) {
    public static AuthSignInResponse of(AuthTokenInfo tokenInfo, AuthMemberInfo memberInfo) {
        return new AuthSignInResponse(tokenInfo, memberInfo);
    }
}
