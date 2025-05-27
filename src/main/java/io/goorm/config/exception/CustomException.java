package io.goorm.config.exception;

public class CustomException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;
    private final Integer status;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.name());
        this.errorCode = errorCode.name();
        this.errorMessage = errorCode.getMessage();
        this.status = errorCode.getStatus().value();
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Integer getStatus() {
        return status;
    }
}
