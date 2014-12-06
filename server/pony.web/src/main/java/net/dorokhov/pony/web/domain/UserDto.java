package net.dorokhov.pony.web.domain;

import java.io.Serializable;

public class UserDto implements Serializable {

	public static enum Role {
		USER, ADMIN
	}

	private Long id;

	private String name;

	private String email;

	private Role role;

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

	public Role getRole() {
		return role;
	}

	public void setRole(Role aRole) {
		role = aRole;
	}
}
