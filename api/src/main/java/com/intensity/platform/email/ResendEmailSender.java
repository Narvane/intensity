package com.intensity.platform.email;

import com.intensity.platform.common.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.Map;

@Component
public class ResendEmailSender implements EmailSender {

	private static final Logger log = LoggerFactory.getLogger(ResendEmailSender.class);
	private static final String RESEND_EMAILS_URL = "https://api.resend.com/emails";

	private final EmailProperties properties;
	private final RestClient restClient;

	public ResendEmailSender(EmailProperties properties) {
		this.properties = properties;
		this.restClient = RestClient.create();
	}

	@Override
	public void send(String to, String subject, String htmlBody) {
		if (!properties.isDeliveryEnabled()) {
			log.info("Email delivery disabled; skipping send to {} — subject: {}", to, subject);
			log.info("Email body:\n{}", htmlBody);
			return;
		}

		try {
			restClient.post()
					.uri(RESEND_EMAILS_URL)
					.contentType(MediaType.APPLICATION_JSON)
					.header("Authorization", "Bearer " + properties.resendApiKey().trim())
					.body(Map.of(
							"from", properties.from(),
							"to", List.of(to),
							"subject", subject,
							"html", htmlBody))
					.retrieve()
					.toBodilessEntity();
		} catch (RestClientResponseException exception) {
			log.error(
					"Resend rejected email to {}: status={} body={}",
					to,
					exception.getStatusCode().value(),
					exception.getResponseBodyAsString());
			throw new ApiException(
					HttpStatus.BAD_GATEWAY,
					"EMAIL_DELIVERY_FAILED",
					"Could not send the email. Try again later.");
		} catch (RuntimeException exception) {
			log.error("Resend request failed for {}", to, exception);
			throw new ApiException(
					HttpStatus.BAD_GATEWAY,
					"EMAIL_DELIVERY_FAILED",
					"Could not send the email. Try again later.");
		}
	}
}
