package net.dorokhov.pony.core.domain.common;

import net.dorokhov.pony.core.domain.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@MappedSuperclass
public abstract class BaseToken {

	private String id;

	private Date date;

	private String secret;

	private User user;

	@Id
	@Column(name = "id", nullable = false)
	@NotNull
	public String getId() {
		return id;
	}

	public void setId(String aId) {
		id = aId;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "date")
	public Date getDate() {
		return date;
	}

	public void setDate(Date aCreationDate) {
		date = aCreationDate;
	}

	@Column(name = "secret")
	@NotNull
	public String getSecret() {
		return secret;
	}

	public void setSecret(String aSecret) {
		secret = aSecret;
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

			BaseToken that = (BaseToken)aObj;

			return id.equals(that.id);
		}

		return false;
	}

	@Override
	public String toString() {
		return "BaseToken{" +
				"id='" + id + '\'' +
				", date=" + date +
				", secret='" + secret + '\'' +
				", user=" + user +
				'}';
	}
}
