package com.optifi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "optifi.defaults")
public record AppDefaults(
        String userCurrency,
        String userLocale,
        String userTimezone
) {
}
