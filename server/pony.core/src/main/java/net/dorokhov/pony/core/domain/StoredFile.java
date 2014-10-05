package net.dorokhov.pony.core.domain;

import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "stored_file", uniqueConstraints = @UniqueConstraint(columnNames = {"tag", "checksum"}))
public class StoredFile extends BaseEntity<Long> {

	private String name;

	private String mimeType;

	private String checksum;

	private String tag;

	private String path;

	private String userData;

	@Column(name = "name")
	@NotBlank
	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	@Column(name = "mime_type")
	@NotBlank
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String aMimeType) {
		mimeType = aMimeType;
	}

	@Column(name = "checksum")
	@NotBlank
	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String aChecksum) {
		checksum = aChecksum;
	}

	@Column(name = "tag")
	public String getTag() {
		return tag;
	}

	public void setTag(String aTag) {
		tag = aTag;
	}

	@Column(name = "path", unique = true)
	@NotBlank
	public String getPath() {
		return path;
	}

	public void setPath(String aPath) {
		path = aPath;
	}

	@Column(name = "user_data")
	public String getUserData() {
		return userData;
	}

	public void setUserData(String aUserData) {
		userData = aUserData;
	}

	@Override
	public String toString() {
		return "StoredFile{" +
				"id=" + getId() +
				", name='" + name + '\'' +
				", mimeType='" + mimeType + '\'' +
				", path='" + path + '\'' +
				", tag='" + tag + '\'' +
				", checksum='" + checksum + '\'' +
				'}';
	}
}
