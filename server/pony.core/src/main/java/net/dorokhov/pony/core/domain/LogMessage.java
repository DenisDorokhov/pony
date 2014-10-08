package net.dorokhov.pony.core.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(name = "log_message")
public class LogMessage {

	public static enum Type {
		DEBUG, INFO, WARN, ERROR
	}

	private Long id;

	private Date date;

	private Type type;

	private String message;

	private String details;

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

	@Column(name = "type")
	@NotNull
	public Type getType() {
		return type;
	}

	public void setType(Type aType) {
		type = aType;
	}

	@Column(name = "message")
	@NotNull
	public String getMessage() {
		return message;
	}

	public void setMessage(String aMessage) {
		message = aMessage;
	}

	@Column(name = "details")
	public String getDetails() {
		return details;
	}

	public void setDetails(String aDetails) {
		details = aDetails;
	}
}
