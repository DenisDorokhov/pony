package net.dorokhov.pony.web.shared;

import org.hibernate.validator.constraints.NotBlank;

public class CredentialsDto {

	private String email;

	private String password;

	@NotBlank
	public String getEmail() {
		return email;
	}

	public void setEmail(String aEmail) {
		email = aEmail;
	}

	@NotBlank
	public String getPassword() {
		return password;
	}

	public void setPassword(String aPassword) {
		password = aPassword;
	}

}
