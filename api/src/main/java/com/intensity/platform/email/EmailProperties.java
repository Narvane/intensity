package com.intensity.platform.email;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "intensity.email")
public record EmailProperties(
		String resendApiKey,
		@NotBlank String from,
		@NotBlank String appBaseUrl,
		@Positive long passwordResetExpirationSeconds) {

	public boolean isDeliveryEnabled() {
		return resendApiKey != null && !resendApiKey.isBlank();
	}

	@AssertTrue(message = "intensity.email.app-base-url must be an absolute http(s) URL")
	public boolean isAppBaseUrlAbsoluteHttp() {
		String value = appBaseUrl == null ? "" : appBaseUrl.trim();
		return (value.startsWith("http://") || value.startsWith("https://"))
				&& !value.contains("${");
	}
}
