package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.AbstractEntity;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "config")
public class Config implements AbstractEntity<String> {

	private String id;

	private Date creationDate;

	private Date updateDate;

	private String value;

	public Config() {
		this(null, null);
	}

	public Config(String aId) {
		this(aId, null);
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
	public Long getLong() {
		return value != null ? Long.valueOf(value) : null;
	}

	public void setLong(Long aValue) {
		value = aValue != null ? String.valueOf(aValue) : null;
	}

	@Transient
	public Integer getInteger() {
		return value != null ? Integer.valueOf(value) : null;
	}

	public void setInteger(Integer aValue) {
		value = aValue != null ? String.valueOf(aValue) : null;
	}

	@Transient
	public Double getDouble() {
		return value != null ? Double.valueOf(value) : null;
	}

	public void setDouble(Double aValue) {
		value = aValue != null ? String.valueOf(aValue) : null;
	}

	@Transient
	public Boolean getBoolean() {
		return value != null ? Boolean.valueOf(value) : null;
	}

	public void setBoolean(Boolean aValue) {
		value = aValue != null ? String.valueOf(aValue) : null;
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
