package net.dorokhov.pony.core.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "installation")
public class Installation extends BaseEntity<Long> {

	private String version;

	@Column(name = "version")
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
