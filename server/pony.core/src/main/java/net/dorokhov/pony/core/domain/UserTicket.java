package net.dorokhov.pony.core.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "user_ticket")
public class UserTicket {

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
	public Date getCreationDate() {
		return date;
	}

	public void setCreationDate(Date aCreationDate) {
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
		setCreationDate(new Date());
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
