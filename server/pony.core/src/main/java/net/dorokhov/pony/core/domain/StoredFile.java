package net.dorokhov.pony.core.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "stored_file", uniqueConstraints = @UniqueConstraint(columnNames = {"tag", "checksum"}))
public class StoredFile {

	public static final String TAG_ARTWORK_EMBEDDED = "artworkEmbedded";
	public static final String TAG_ARTWORK_FILE = "artworkFile";

	private Long id;

	private Date date;

	private String name;

	private String mimeType;

	private String checksum;

	private Long size;

	private String tag;

	private String path;

	private String userData;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long aId) {
		id = aId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date aDate) {
		date = aDate;
	}

	@Column(name = "name", nullable = false)
	@NotNull
	public String getName() {
		return name;
	}

	public void setName(String aName) {
		name = aName;
	}

	@Column(name = "mime_type", nullable = false)
	@NotNull
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String aMimeType) {
		mimeType = aMimeType;
	}

	@Column(name = "checksum", nullable = false)
	@NotNull
	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String aChecksum) {
		checksum = aChecksum;
	}

	@Column(name = "size", nullable = false)
	@NotNull
	public Long getSize() {
		return size;
	}

	public void setSize(Long aSize) {
		size = aSize;
	}

	@Column(name = "tag")
	public String getTag() {
		return tag;
	}

	public void setTag(String aTag) {
		tag = aTag;
	}

	@Column(name = "path", nullable = false, unique = true)
	@NotNull
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

	@PrePersist
	public void prePersist() {
		setDate(new Date());
	}

	@Override
	public int hashCode() {
		return id != null ? id.hashCode() : super.hashCode();
	}

	@Override
	public boolean equals(Object aObj) {

		if (this == aObj) {
			return true;
		}

		if (aObj != null && id != null && getClass().equals(aObj.getClass())) {

			StoredFile that = (StoredFile) aObj;

			return id.equals(that.id);
		}

		return false;
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
				", userData='" + userData + '\'' +
				'}';
	}

}
