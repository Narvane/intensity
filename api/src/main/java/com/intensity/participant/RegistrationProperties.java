package com.intensity.participant;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "intensity.registration")
public record RegistrationProperties(boolean allowlistEnabled) {
}
