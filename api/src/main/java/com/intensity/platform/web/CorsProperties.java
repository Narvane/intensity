package com.intensity.platform.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "intensity.cors")
public record CorsProperties(List<String> allowedOriginPatterns) {
}
