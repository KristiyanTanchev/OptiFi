package com.optifi.exceptions;

import lombok.Getter;

@Getter
public class EnumParsingError extends RuntimeException {
    private final String field;
    private final String error;

    public EnumParsingError(String field, String error) {
        super(error);
        this.field = field;
        this.error = error;
    }
}
