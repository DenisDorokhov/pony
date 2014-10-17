package net.dorokhov.pony.core.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "log_message")
public class LogMessage {

	public static enum Type {
		DEBUG, INFO, WARN, ERROR
	}

	private Long id;

	private Date date;

	private Type type;

	private String messageCode;

	private String messageDetails;

	private List<String> messageArguments;

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

	@Column(name = "message_code")
	@NotNull
	public String getMessageCode() {
		return messageCode;
	}

	public void setMessageCode(String aMessage) {
		messageCode = aMessage;
	}

	@Column(name = "message_details")
	public String getMessageDetails() {
		return messageDetails;
	}

	public void setMessageDetails(String aMessageDetails) {
		messageDetails = aMessageDetails;
	}

	@Column(name = "value")
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "log_message_arguments", joinColumns = @JoinColumn(name="log_message_id"))
	public List<String> getMessageArguments() {

		if (messageArguments == null) {
			messageArguments = new ArrayList<>();
		}

		return messageArguments;
	}

	public void setMessageArguments(List<String> aArguments) {
		messageArguments = aArguments;
	}
}
