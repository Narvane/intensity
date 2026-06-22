package com.intensity.participant.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "allowlist_email")
public class AllowlistEmail {

	@Id
	@Column(nullable = false)
	private String email;

	protected AllowlistEmail() {
	}

	public AllowlistEmail(String email) {
		this.email = email.toLowerCase();
	}

	public String getEmail() {
		return email;
	}
}
