package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.common.SqlSplitter;
import net.dorokhov.pony.core.domain.Installation;
import org.apache.commons.io.IOUtils;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;

@Repository
public class InstallationDaoImpl implements InstallationDao {

	public final static String SCRIPT_INSTALL = "/net/dorokhov/pony/core/dao/install.sql";
	public final static String SCRIPT_UNINSTALL = "/net/dorokhov/pony/core/dao/uninstall.sql";

	private DataSource dataSource;

	private EntityManager entityManager;

	@Autowired
	public void setDataSource(DataSource aDataSource) {
		dataSource = aDataSource;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager aEntityManager) {
		entityManager = aEntityManager;
	}

	@Override
	@Transactional
	public Installation findInstallation() {

		Installation installation = null;

		try {
			installation = doFindInstallation();
		} catch (Exception e) {
			if (!(e.getCause() instanceof SQLGrammarException)) { // Accept errors because of not created tables.
				throw new RuntimeException(e);
			}
		}

		return installation;
	}

	@Override
	@Transactional
	public Installation install() {

		try {

			SqlSplitter splitter = new SqlSplitter();

			for (String statement : splitter.splitScript(fetchScriptContents(SCRIPT_INSTALL))) {
				entityManager.createNativeQuery(statement).executeUpdate();
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		return doFindInstallation();
	}

	@Override
	@Transactional
	public void uninstall() {

		try {

			SqlSplitter splitter = new SqlSplitter();

			for (String statement : splitter.splitScript(fetchScriptContents(SCRIPT_UNINSTALL))) {
				entityManager.createNativeQuery(statement).executeUpdate();
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Installation doFindInstallation() {

		if (!hasInstallationTable()) {
			return null;
		}

		Installation installation = null;

		try {
			installation = entityManager.createQuery("SELECT i FROM Installation i", Installation.class).getSingleResult();
		} catch (NoResultException e) {
			// No result means no installation.
		}

		return installation;
	}

	private boolean hasInstallationTable() {

		Connection connection = null;

		try {

			String tableName = Installation.class.getAnnotation(Table.class).name();

			connection = dataSource.getConnection();

			ResultSet rs = connection.getMetaData().getTables(null, null, "%", null);
			while (rs.next()) {
				if (rs.getString(3).equalsIgnoreCase(tableName)) {
					return true;
				}
			}

			return false;

		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (connection != null) {
				//noinspection EmptyCatchBlock
				try {
					connection.close();
				} catch (Exception e) {}
			}
		}
	}

	private String fetchScriptContents(String aScript) throws Exception {

		InputStream inputStream = this.getClass().getResourceAsStream(aScript);

		if (inputStream == null) {
			throw new Exception("Script not found.");
		}

		return IOUtils.toString(inputStream, "UTF-8");
	}
}
