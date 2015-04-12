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

	private String code;

	private String text;

	private String details;

	private List<LogMessageArgument> arguments;

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
	@NotNull
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

	@Column(name = "code")
	@NotNull
	public String getCode() {
		return code;
	}

	public void setCode(String aMessage) {
		code = aMessage;
	}

	@Column(name = "text")
	public String getText() {
		return text;
	}

	public void setText(String aText) {
		text = aText;
	}

	@Column(name = "details")
	public String getDetails() {
		return details;
	}

	public void setDetails(String aMessageDetails) {
		details = aMessageDetails;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "logMessage")
	@OrderBy("sort ASC")
	public List<LogMessageArgument> getArguments() {

		if (arguments == null) {
			arguments = new ArrayList<>();
		}

		return arguments;
	}

	public void setArguments(List<LogMessageArgument> aArguments) {
		arguments = aArguments;
	}

	@PrePersist
	public void prePersist() {
		if (getDate() == null) {
			setDate(new Date());
		}
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

			LogMessage that = (LogMessage)aObj;

			return id.equals(that.id);
		}

		return false;
	}

	@Override
	public String toString() {
		return "LogMessage{" +
				"id=" + id +
				", date=" + date +
				", type=" + type +
				", code='" + code + '\'' +
				", text='" + text + '\'' +
				", details='" + details + '\'' +
				", arguments=" + arguments +
				'}';
	}

}
