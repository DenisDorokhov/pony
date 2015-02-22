package net.dorokhov.pony.web.shared;

public class UserDto extends AbstractDto<Long> {

	private String name;

	private String email;

	private RoleDto role;

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

	public RoleDto getRole() {
		return role;
	}

	public void setRole(RoleDto aRole) {
		role = aRole;
	}

}
