package com.optifi.exceptions;

public class SamePasswordException extends RuntimeException {
    public SamePasswordException() {
        super("Old and new passwords are the same.");
    }
}
