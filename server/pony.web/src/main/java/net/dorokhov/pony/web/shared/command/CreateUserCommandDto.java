package net.dorokhov.pony.web.shared.command;

import net.dorokhov.pony.web.shared.RoleDto;
import net.dorokhov.pony.web.server.validation.UniqueUserEmail;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@UniqueUserEmail
public class CreateUserCommandDto {

	private String name;

	private String email;

	private String password;

	private RoleDto role;

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
	@Size(max = 255)
	public String getPassword() {
		return password;
	}

	public void setPassword(String aPassword) {
		password = aPassword;
	}

	@NotNull
	public RoleDto getRole() {
		return role;
	}

	public void setRole(RoleDto aRole) {
		role = aRole;
	}

}
