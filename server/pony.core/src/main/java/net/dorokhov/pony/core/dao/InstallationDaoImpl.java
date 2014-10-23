package net.dorokhov.pony.core.dao;

import net.dorokhov.pony.core.entity.Installation;
import net.dorokhov.pony.core.utils.SqlSplitter;
import org.apache.commons.io.IOUtils;
import org.hibernate.exception.SQLGrammarException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import java.io.InputStream;
import java.util.List;

@Repository
public class InstallationDaoImpl implements InstallationDao {

	public final static String SCRIPT_INSTALL = "/net/dorokhov/pony/core/dao/install.sql";
	public final static String SCRIPT_UNINSTALL = "/net/dorokhov/pony/core/dao/uninstall.sql";

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager aEntityManager) {
		entityManager = aEntityManager;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
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

		List<Installation> installationList = entityManager.createQuery("SELECT i FROM Installation i", Installation.class).getResultList();

		if (installationList.size() > 1) {
			throw new NonUniqueResultException();
		}

		return installationList.size() > 0 ? installationList.get(0) : null;
	}

	private String fetchScriptContents(String aScript) throws Exception {

		InputStream inputStream = this.getClass().getResourceAsStream(aScript);

		if (inputStream == null) {
			throw new Exception("Script not found.");
		}

		return IOUtils.toString(inputStream, "UTF-8");
	}

}
