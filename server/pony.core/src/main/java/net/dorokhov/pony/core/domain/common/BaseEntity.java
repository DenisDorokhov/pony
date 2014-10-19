package net.dorokhov.pony.core.domain.common;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@MappedSuperclass
public abstract class BaseEntity<T extends Serializable> implements AbstractEntity<T> {

	private T id;

	private Date creationDate;

	private Date updateDate;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public T getId() {
		return id;
	}

	public void setId(T aId) {
		id = aId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creation_date")
	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date aCreationDate) {
		creationDate = aCreationDate;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "update_date")
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date aUpdateDate) {
		updateDate = aUpdateDate;
	}

	@PrePersist
	public void prePersist() {
		setCreationDate(new Date());
		setUpdateDate(new Date());
	}

	@PreUpdate
	public void preUpdate() {
		setUpdateDate(new Date());
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

			BaseEntity that = (BaseEntity) aObj;

			return id.equals(that.id);
		}

		return false;
	}
}
