package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "role")
public class Role extends BaseEntity<Long> {

	public static final String USER = "user";
	public static final String ADMIN = "admin";

	private String name;

	@Column(name = "name")
	@NotNull
	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}
}
