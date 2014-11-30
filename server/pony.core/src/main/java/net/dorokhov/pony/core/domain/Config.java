package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.AbstractEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "config")
public class Config implements AbstractEntity<String> {

	public static final String AUTO_SCAN_INTERVAL = "autoScanInterval";
	public static final String LIBRARY_FOLDERS = "libraryFolders";

	private String id;

	private Date creationDate;

	private Date updateDate;

	private String value;

	public Config() {
		this(null, null);
	}

	public Config(String aId, String aValue) {
		setId(aId);
		setValue(aValue);
	}

	public Config(String aId, int aValue) {
		this(aId, String.valueOf(aValue));
	}

	public Config(String aId, double aValue) {
		this(aId, String.valueOf(aValue));
	}

	public Config(String aId, boolean aValue) {
		this(aId, String.valueOf(aValue));
	}

	@Override
	@Id
	@Column(name = "id", nullable = false)
	public String getId() {
		return id;
	}

	@Override
	public void setId(String aId) {
		id = aId;
	}

	@Override
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	@Override
	public void setCreationDate(Date aCreationDate) {
		creationDate = aCreationDate;
	}

	@Override
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_date")
	public Date getUpdateDate() {
		return updateDate;
	}

	@Override
	public void setUpdateDate(Date aUpdateDate) {
		updateDate = aUpdateDate;
	}

	@Column(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String aValue) {
		value = aValue;
	}

	@PrePersist
	public void prePersist() {
		setCreationDate(new Date());
	}

	@PreUpdate
	public void preUpdate() {
		setUpdateDate(new Date());
	}

	@Transient
	public long getLong() {
		return Long.valueOf(value);
	}

	public void setLong(long aValue) {
		value = String.valueOf(aValue);
	}

	@Transient
	public int getInteger() {
		return Integer.valueOf(value);
	}

	public void setInteger(int aValue) {
		value = String.valueOf(aValue);
	}

	@Transient
	public double getDouble() {
		return Double.valueOf(value);
	}

	public void setDouble(double aValue) {
		value = String.valueOf(aValue);
	}

	@Transient
	public boolean getBoolean() {
		return Boolean.valueOf(value);
	}

	public void setBoolean(boolean aValue) {
		value = Boolean.toString(aValue);
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

			Config that = (Config)aObj;

			return id.equals(that.id);
		}

		return false;
	}

	@Override
	public String toString() {
		return "Configuration{" +
				"id=" + getId() +
				", value='" + value + '\'' +
				'}';
	}
}
