package br.com.narvane.config.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class WebConfig {

    /**
     * CORS usado pelo Spring Security ({@code http.cors()}). Lista separada por vírgula ou property YAML
     * {@code app.cors.allowed-origin-patterns} como lista.
     * Inclui produção (narvane.com.br + www) e padrões locais; "*" cobre outros casos com allowCredentials false.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.cors.allowed-origin-patterns:https://narvane.com.br,https://www.narvane.com.br,http://localhost:*,http://127.0.0.1:*,*}") String allowedOriginPatternsProp) {
        var config = new CorsConfiguration();
        config.setAllowCredentials(false);
        List<String> patterns = Arrays.stream(allowedOriginPatternsProp.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .collect(Collectors.toList());
        config.setAllowedOriginPatterns(patterns);
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));
        config.setMaxAge(3600L);
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
