package com.intensity.platform.web;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableConfigurationProperties(CorsProperties.class)
public class CorsConfig {

	@Bean
	CorsConfigurationSource corsConfigurationSource(CorsProperties properties) {
		List<String> patterns = properties.allowedOriginPatterns();
		if (patterns == null || patterns.isEmpty()) {
			patterns = List.of("http://localhost", "http://localhost:*");
		}

		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOriginPatterns(patterns);
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setExposedHeaders(List.of("Authorization"));
		// Bearer tokens in Authorization — cookies are not used. Credentials mode
		// makes CORS stricter in WebViews and is unnecessary for this API.
		configuration.setAllowCredentials(false);
		configuration.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
