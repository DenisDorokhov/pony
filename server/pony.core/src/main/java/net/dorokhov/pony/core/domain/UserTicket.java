package net.dorokhov.pony.core.domain;

import net.dorokhov.pony.core.domain.common.AbstractEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "user_ticket")
public class UserTicket implements AbstractEntity<String> {

	private String id;

	private Date creationDate;

	private Date updateDate;

	private User user;

	@Override
	@Id
	@Column(name = "id", nullable = false)
	@NotNull
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

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	@NotNull
	public User getUser() {
		return user;
	}

	public void setUser(User aUser) {
		user = aUser;
	}

	@PrePersist
	public void prePersist() {
		setCreationDate(new Date());
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

			UserTicket that = (UserTicket)aObj;

			return id.equals(that.id);
		}

		return false;
	}
}
