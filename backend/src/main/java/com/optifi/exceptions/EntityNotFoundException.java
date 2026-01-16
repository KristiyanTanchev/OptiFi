package com.optifi.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String type, long id) {
        this(type, "id", String.valueOf(id));
    }

    public EntityNotFoundException(String entity, String attribute, String value) {
        this(String.format("%s with %s %s not found.", entity, attribute, value));
    }
}
