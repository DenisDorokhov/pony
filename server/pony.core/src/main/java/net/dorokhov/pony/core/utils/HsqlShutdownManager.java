package net.dorokhov.pony.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;

public class HsqlShutdownManager {

	private final Logger log = LoggerFactory.getLogger(getClass());

	private DataSource dataSource;

	public void setDataSource(DataSource aDataSource) {
		dataSource = aDataSource;
	}

	public void shutdown() {

		Connection connection = null;
		try {

			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

			connection = DataSourceUtils.getConnection(jdbcTemplate.getDataSource());
			connection.setAutoCommit(true);

			jdbcTemplate.execute("SHUTDOWN");

		} catch(Exception e) {
			log.error("could not shutdown database", e);
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch(Exception e) {
				log.error("could not close database connection", e);
			}
		}
	}

}
