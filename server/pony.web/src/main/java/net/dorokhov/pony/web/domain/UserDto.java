package net.dorokhov.pony.web.domain;

import net.dorokhov.pony.core.domain.User;

public class UserDto {

	private Long id;

	private String name;

	private String email;

	private RoleDto role;

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

	public RoleDto getRole() {
		return role;
	}

	public void setRole(RoleDto aRole) {
		role = aRole;
	}

	public static UserDto valueOf(User aUser) {

		UserDto dto = new UserDto();

		dto.setId(aUser.getId());
		dto.setName(aUser.getName());
		dto.setEmail(aUser.getEmail());

		if (aUser.getRoles().contains(RoleDto.ADMIN.toString())) {
			dto.setRole(RoleDto.ADMIN);
		} else if (aUser.getRoles().contains(RoleDto.USER.toString())) {
			dto.setRole(RoleDto.USER);
		}

		return dto;
	}
}
