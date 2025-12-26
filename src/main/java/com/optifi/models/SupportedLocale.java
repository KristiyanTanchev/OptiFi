package com.optifi.models;

public enum SupportedLocale {
    EN_US("en-US"),
    EN_GB("en-GB"),
    DE_DE("bg-BG");

    private final String tag;

    SupportedLocale(String tag) {
        this.tag = tag;
    }

    public String tag() {
        return tag;
    }
}
