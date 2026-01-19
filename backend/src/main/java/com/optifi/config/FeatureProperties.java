package com.optifi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "optifi.features")
public record FeatureProperties(
        boolean registrationEnabled
) {}
