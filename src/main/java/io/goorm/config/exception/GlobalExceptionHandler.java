package io.goorm.config.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<GlobalExceptionResponse> handleCustomException(CustomException ex) {
        String errorCode = ex.getErrorCode();
        String errorMessage = ex.getErrorMessage();

        return ResponseEntity
                .status(ex.getStatus())
                .body(GlobalExceptionResponse.of(errorMessage, errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GlobalExceptionResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage() != null ?
                ex.getBindingResult().getFieldError().getDefaultMessage()
                : ErrorCode.COMMON_BAD_REQUEST.getMessage();

        String errorCode = ErrorCode.COMMON_BAD_REQUEST.name();

        return ResponseEntity
                .status(ErrorCode.COMMON_BAD_REQUEST.getStatus())
                .body(GlobalExceptionResponse.of(errorMessage,errorCode));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<GlobalExceptionResponse> handleException(Exception ex) {

        log.info("Exception: {}", ex.getMessage());

        return ResponseEntity
                .status(ErrorCode.COMMON_INTERNAL_SERVER_ERROR.getStatus())
                .body(GlobalExceptionResponse.of(ErrorCode.COMMON_INTERNAL_SERVER_ERROR.getMessage(),ErrorCode.COMMON_INTERNAL_SERVER_ERROR.name()));
    }
}
