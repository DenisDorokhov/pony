package net.dorokhov.pony.core.version;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Service
public class VersionProviderImpl implements VersionProvider {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH);

	private String version;

	private Date date;

	@Override
	public String getVersion() {
		return version;
	}

	@Value("${pony.version}")
	public void setVersion(String aVersion) {
		version = aVersion;
	}

	@Override
	public Date getDate() {
		return date;
	}

	@Value("${pony.date}")
	public void setDate(String aDate) throws ParseException {
		date = DATE_FORMAT.parse(aDate);
	}

}
