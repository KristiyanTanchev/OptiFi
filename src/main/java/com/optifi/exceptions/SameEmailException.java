package com.optifi.exceptions;

public class SameEmailException extends RuntimeException {
    public SameEmailException() {
        super("New email matches the old.");
    }
}
