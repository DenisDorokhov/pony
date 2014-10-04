package net.dorokhov.pony.web.service;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class DatabaseShutdownListener implements ApplicationListener<ContextClosedEvent> {

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager aEntityManager) {
		entityManager = aEntityManager;
	}

	@Override
	@Transactional
	public void onApplicationEvent(ContextClosedEvent aEvent) {
		if (aEvent.getApplicationContext().getParent() == null) {
			entityManager.createNativeQuery("SHUTDOWN").executeUpdate();
		}
	}
}
