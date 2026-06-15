package com.intensity.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "intensity.cors")
public record CorsProperties(List<String> allowedOriginPatterns) {
}
