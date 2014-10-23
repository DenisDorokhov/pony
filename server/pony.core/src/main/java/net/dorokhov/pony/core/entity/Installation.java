package net.dorokhov.pony.core.entity;

import net.dorokhov.pony.core.entity.common.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "installation")
public class Installation extends BaseEntity<Long> {

	private String version;

	@Column(name = "version", nullable = false)
	@NotNull
	public String getVersion() {
		return version;
	}

	public void setVersion(String aVersion) {
		version = aVersion;
	}

	@Override
	public String toString() {
		return "Installation{" +
				"id=" + getId() +
				", version='" + version + '\'' +
				'}';
	}
}
