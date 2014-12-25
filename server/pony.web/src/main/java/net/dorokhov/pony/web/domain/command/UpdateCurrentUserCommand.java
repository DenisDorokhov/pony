package net.dorokhov.pony.web.domain.command;

import net.dorokhov.pony.web.validation.UniqueUserEmail;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

@UniqueUserEmail
public class UpdateCurrentUserCommand {

	private String name;

	private String email;

	private String oldPassword;

	private String newPassword;

	@NotBlank
	@Size(max = 255)
	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	@NotBlank
	@Email
	@Size(max = 255)
	public String getEmail() {
		return email;
	}

	public void setEmail(String aEmail) {
		email = aEmail;
	}

	@NotBlank
	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String aOldPassword) {
		oldPassword = aOldPassword;
	}

	@Size(max = 255)
	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String aNewPassword) {
		newPassword = aNewPassword;
	}

}
