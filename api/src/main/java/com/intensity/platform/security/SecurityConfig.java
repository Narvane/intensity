package com.intensity.platform.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			CorsConfigurationSource corsConfigurationSource,
			JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
		http
				.cors(cors -> cors.configurationSource(corsConfigurationSource))
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						// Ant matchers (not MVC): MVC matchers only succeed when a controller
						// mapping also matches Content-Type, so a missing/odd Content-Type on
						// public POSTs fell through to authenticated → false INVALID_TOKEN.
						.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.OPTIONS, "/**"))
						.permitAll()
						.requestMatchers(
								AntPathRequestMatcher.antMatcher("/actuator/health"),
								AntPathRequestMatcher.antMatcher("/v3/api-docs"),
								AntPathRequestMatcher.antMatcher("/v3/api-docs/**"),
								AntPathRequestMatcher.antMatcher("/swagger-ui/**"),
								AntPathRequestMatcher.antMatcher("/swagger-ui.html"),
								AntPathRequestMatcher.antMatcher("/openapi.yaml"))
						.permitAll()
						.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/v1/auth/**"))
						.permitAll()
						.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/v1/participants"))
						.permitAll()
						.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/v1/invites/validate"))
						.permitAll()
						.anyRequest().authenticated())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, exception) -> {
					response.setStatus(401);
					response.setContentType(MediaType.APPLICATION_JSON_VALUE);
					// Ensure browsers/WebViews can read 401 bodies cross-origin.
					String origin = request.getHeader("Origin");
					if (origin != null && !origin.isBlank()) {
						response.setHeader("Access-Control-Allow-Origin", origin);
						response.setHeader("Vary", "Origin");
					}
					response.getWriter().write("""
							{"code":"INVALID_TOKEN","message":"Invalid or expired token."}
							""");
				}))
				.httpBasic(basic -> basic.disable())
				.formLogin(form -> form.disable());

		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
