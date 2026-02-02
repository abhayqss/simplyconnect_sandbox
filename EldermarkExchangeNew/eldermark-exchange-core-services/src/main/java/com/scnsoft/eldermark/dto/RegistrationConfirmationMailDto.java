package com.scnsoft.eldermark.dto;

public class RegistrationConfirmationMailDto {

	private String toEmail;

	private String fullName;
	private String username;
	private String companyId;
	private String passwordResetUrl;

	public String getToEmail() {
		return toEmail;
	}

	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getPasswordResetUrl() {
		return passwordResetUrl;
	}

	public void setPasswordResetUrl(String passwordResetUrl) {
		this.passwordResetUrl = passwordResetUrl;
	}
}
