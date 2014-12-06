package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user")
public class User extends BaseEntity<Long> {

	private String name;

	private String email;

	private String password;

	private List<Role> roles;

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	@Column(name = "email", unique = true)
	@NotNull
	public String getEmail() {
		return email;
	}

	public void setEmail(String aLogin) {
		email = aLogin;
	}

	@Column(name = "password")
	@NotNull
	public String getPassword() {
		return password;
	}

	public void setPassword(String aPassword) {
		password = aPassword;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_role",
			joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
	public List<Role> getRoles() {

		if (roles == null) {
			roles = new ArrayList<>();
		}

		return roles;
	}

	public void setRoles(List<Role> aRoles) {
		roles = aRoles;
	}

}
