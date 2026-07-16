package com.intensity.platform.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

/**
 * Single source of truth for routes that must never require authentication.
 *
 * <p>Matchers are Ant-based on purpose: MVC matchers only succeed when a
 * controller mapping also matches (including Content-Type negotiation), so a
 * public POST with a missing or odd Content-Type would fall through to
 * {@code authenticated()} and surface a misleading {@code INVALID_TOKEN}.
 */
public final class PublicApiPaths {

	private static final List<RequestMatcher> MATCHERS = List.of(
			AntPathRequestMatcher.antMatcher(HttpMethod.OPTIONS, "/**"),
			AntPathRequestMatcher.antMatcher("/actuator/health"),
			AntPathRequestMatcher.antMatcher("/v3/api-docs"),
			AntPathRequestMatcher.antMatcher("/v3/api-docs/**"),
			AntPathRequestMatcher.antMatcher("/swagger-ui/**"),
			AntPathRequestMatcher.antMatcher("/swagger-ui.html"),
			AntPathRequestMatcher.antMatcher("/openapi.yaml"),
			AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/v1/auth/**"),
			AntPathRequestMatcher.antMatcher(HttpMethod.POST, "/v1/participants"),
			AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/v1/invites/validate"));

	private PublicApiPaths() {
	}

	public static RequestMatcher[] requestMatchers() {
		return MATCHERS.toArray(RequestMatcher[]::new);
	}

	public static boolean isPublic(HttpServletRequest request) {
		return MATCHERS.stream().anyMatch(matcher -> matcher.matches(request));
	}
}
