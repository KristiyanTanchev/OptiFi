package com.optifi.domain.account.model;

public enum AccountType {
    CASH, BANK;

    @Override
    public String toString() {
        return switch (this) {
            case CASH -> "Cash";
            case BANK -> "Bank";
        };
    }
}
