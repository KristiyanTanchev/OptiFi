package com.optifi.exceptions;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "Resource not found"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "Access denied"),
    VALIDATION(HttpStatus.BAD_REQUEST, "Validation failed"),
    DUPLICATE(HttpStatus.CONFLICT, "Duplicate resource"),
    ILLEGAL_STATE(HttpStatus.CONFLICT, "Illegal state"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "Bad request"),
    DATA_INTEGRITY(HttpStatus.CONFLICT, "Operation violates data constraints"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");

    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(HttpStatus status, String defaultMessage) {
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public HttpStatus status() {
        return status;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}
