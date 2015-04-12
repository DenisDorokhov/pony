package net.dorokhov.pony.web.shared.command;

import net.dorokhov.pony.web.server.validation.RepeatPassword;
import net.dorokhov.pony.web.server.validation.UniqueUserEmail;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;

@UniqueUserEmail
@RepeatPassword
public class UpdateCurrentUserCommandDto {

	private String name;

	private String email;

	private String oldPassword;

	private String newPassword;

	private String repeatNewPassword;

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

	public String getRepeatNewPassword() {
		return repeatNewPassword;
	}

	public void setRepeatNewPassword(String aRepeatNewPassword) {
		repeatNewPassword = aRepeatNewPassword;
	}

}
