package net.dorokhov.pony.web.domain.command;

import net.dorokhov.pony.web.domain.UserDto;

public class UpdateUserCommand {

	private Long id;

	private String name;

	private String email;

	private String password;

	private UserDto.Role role;

	public Long getId() {
		return id;
	}

	public void setId(Long aId) {
		id = aId;
	}

	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String aEmail) {
		email = aEmail;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String aPassword) {
		password = aPassword;
	}

	public UserDto.Role getRole() {
		return role;
	}

	public void setRole(UserDto.Role aRole) {
		role = aRole;
	}
}
