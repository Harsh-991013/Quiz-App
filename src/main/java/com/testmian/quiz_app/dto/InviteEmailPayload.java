package com.testmian.quiz_app.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InviteEmailPayload {
    private String email;
    private String fullName;
    private String magicToken;
    private LocalDateTime expiresAt;
    
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	public String getMagicToken() {
		return magicToken;
	}
	public void setMagicToken(String magicToken) {
		this.magicToken = magicToken;
	}
	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}
	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

   
}
