package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.BaseEntity;
import org.hibernate.validator.constraints.Email;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user")
public class User extends BaseEntity<Long> {

	private String name;

	private String email;

	private String password;

	private Set<String> roles;

	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	@Column(name = "email", unique = true)
	@Email
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

	@Column(name="value")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name="user_role", joinColumns = @JoinColumn(name = "user_id"))
	public Set<String> getRoles() {

		if (roles == null) {
			roles = new HashSet<>();
		}

		return roles;
	}

	public void setRoles(Set<String> aRoles) {
		roles = aRoles;
	}

	@Override
	public String toString() {
		return "User{" +
				"name='" + name + '\'' +
				", email='" + email + '\'' +
				", roles=" + roles +
				'}';
	}

	@PrePersist
	@PreUpdate
	public void normalize() {
		setEmail(getEmail() != null ? getEmail().trim() : null);
	}

}
