package com.intensity.platform.email;

/**
 * Outbound transactional email. Implementations may call an external provider
 * or log locally when email delivery is disabled.
 */
public interface EmailSender {

	void send(String to, String subject, String htmlBody);
}
