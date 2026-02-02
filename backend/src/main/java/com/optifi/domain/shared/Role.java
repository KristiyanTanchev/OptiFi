package com.optifi.domain.shared;

public enum Role {
    ADMIN, MODERATOR, USER, WAITING_APPROVAL, BLOCKED;

    @Override
    public String toString() {
        return switch (this) {
            case ADMIN -> "Admin";
            case MODERATOR -> "Moderator";
            case USER -> "User";
            case WAITING_APPROVAL -> "Waiting for approval";
            case BLOCKED -> "Blocked";
        };
    }
}

