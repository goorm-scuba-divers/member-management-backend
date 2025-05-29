package io.goorm.config.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    // 공통
    COMMON_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    COMMON_BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 인증
    AUTH_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 사용자입니다."),
    AUTH_USERNAME_PASSWORD_INVALID(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 잘못되었습니다."),
    AUTH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

    // 멤버
    MEMBER_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 회원입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    MEMBER_PASSWORD_SAME_AS_PREVIOUS(HttpStatus.BAD_REQUEST, "이전 비밀번호와 같습니다."),
    MEMBER_PASSWORD_INVALID(HttpStatus.BAD_REQUEST, "비밀번호가 유효하지 않습니다."),
    ;
    private HttpStatus status;
    private String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
