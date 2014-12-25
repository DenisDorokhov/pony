package net.dorokhov.pony.core.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "log_message_argument")
public class LogMessageArgument {

	private Long id;

	private Integer sort;

	private String value;

	private LogMessage logMessage;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	public Long getId() {
		return id;
	}

	public void setId(Long aId) {
		id = aId;
	}

	@Column(name="sort")
	@NotNull
	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer aSort) {
		sort = aSort;
	}

	@Column(name="value")
	public String getValue() {
		return value;
	}

	public void setValue(String aValue) {
		value = aValue;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "log_message_id", nullable = false)
	@NotNull
	public LogMessage getLogMessage() {
		return logMessage;
	}

	public void setLogMessage(LogMessage aLogMessage) {
		logMessage = aLogMessage;
	}

	@Override
	public String toString() {
		return value;
	}

}
