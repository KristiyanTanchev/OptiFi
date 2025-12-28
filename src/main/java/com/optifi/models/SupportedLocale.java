package com.optifi.models;

import com.optifi.exceptions.EnumParsingError;

public enum SupportedLocale {
    EN_US("en-US"),
    EN_GB("en-GB"),
    BG_BG("bg-BG");

    private final String tag;

    SupportedLocale(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }

    public static SupportedLocale fromString(String value) {
        for (SupportedLocale locale : values()) {
            if (locale.tag.equalsIgnoreCase(value)) {
                return locale;
            }
        }

        throw new EnumParsingError("locale", "Unsupported locale: " + value);
    }
}
