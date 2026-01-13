package com.optifi.exceptions;

import java.util.Map;

public class ApiException extends RuntimeException {
    private final ErrorCode code;
    private final Map<String, Object> details;

    public ApiException(ErrorCode code) {
        super(code.defaultMessage());
        this.code = code;
        this.details = Map.of();
    }

    public ApiException(ErrorCode code, String message) {
        super(message);
        this.code = code;
        this.details = Map.of();
    }

    public ApiException(ErrorCode code, String message, Map<String, Object> details) {
        super(message);
        this.code = code;
        this.details = details == null ? Map.of() : Map.copyOf(details);
    }

    public ErrorCode code() {
        return code;
    }

    public Map<String, Object> details() {
        return details;
    }
}

