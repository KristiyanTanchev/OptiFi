package com.optifi.exceptions;

public class DuplicateEntityException extends RuntimeException {
    public DuplicateEntityException(String message) {
        super(message);
    }

    public DuplicateEntityException(String entity, String attribute, String value) {
        this(String.format("%s with %s %s already exists.", entity, attribute, value));
    }
}
