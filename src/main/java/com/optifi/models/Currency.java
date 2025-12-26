package com.optifi.models;

public enum Currency {
    USD, EUR;

    @Override
    public String toString() {
        return switch (this) {
            case USD -> "dollars";
            case EUR -> "euro";
        };
    }
}
