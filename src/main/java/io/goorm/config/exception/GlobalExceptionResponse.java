package io.goorm.config.exception;

public record GlobalExceptionResponse(
        String message,
        String errorCode
) {

    public static GlobalExceptionResponse of(String message, String errorCode) {
        return new GlobalExceptionResponse(message, errorCode);
    }
}
