package com.optifi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class OptiFiApplication {

    public static void main(String[] args) {
        SpringApplication.run(OptiFiApplication.class, args);
    }

}
