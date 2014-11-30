package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.BaseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

@Entity
@Table(name = "user")
public class User extends BaseEntity<Long> implements UserDetails {

	private String fullName;

	private String username;

	private String password;

	private List<Role> roles;

	@Column(name = "full_name")
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String aName) {
		fullName = aName;
	}

	@Override
	@Column(name = "username", unique = true)
	@NotNull
	public String getUsername() {
		return username;
	}

	public void setUsername(String aLogin) {
		username = aLogin;
	}

	@Override
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

	@Override
	@Transient
	public Collection<? extends GrantedAuthority> getAuthorities() {

		Set<GrantedAuthority> authorities = new HashSet<>();

		for (Role role : getRoles()) {
			authorities.add(new SimpleGrantedAuthority(role.getName()));
		}

		return authorities;
	}

	@Override
	@Transient
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	@Transient
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	@Transient
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	@Transient
	public boolean isEnabled() {
		return true;
	}
}
