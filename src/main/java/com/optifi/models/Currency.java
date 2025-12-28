package com.optifi.models;

import com.optifi.exceptions.EnumParsingError;

public enum Currency {
    USD, EUR;

    public static Currency fromString(String value) {
        for (Currency currency : values()) {
            if (currency.name().equalsIgnoreCase(value)) {
                return currency;
            }
        }

        throw new EnumParsingError("currency", "Unsupported currency: " + value);
    }
}
