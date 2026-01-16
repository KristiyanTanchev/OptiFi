package com.optifi.domain.account.model;

import com.optifi.exceptions.EnumParsingError;

import java.util.Locale;

public enum AccountType {
    CASH, BANK;

    @Override
    public String toString() {
        return switch (this) {
            case CASH -> "Cash";
            case BANK -> "Bank";
        };
    }

    public static AccountType fromString(String value) {
        String normalized = value.trim();

        for (AccountType type : values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }

        throw new EnumParsingError("accountType", "Unsupported account type: " + value);
    }
}
