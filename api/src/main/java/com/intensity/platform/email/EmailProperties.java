package com.intensity.platform.email;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "intensity.email")
public record EmailProperties(
		String resendApiKey,
		String from,
		String appBaseUrl,
		long passwordResetExpirationSeconds) {

	public boolean isDeliveryEnabled() {
		return resendApiKey != null && !resendApiKey.isBlank();
	}
}
