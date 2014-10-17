package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.domain.LogMessage;
import net.dorokhov.pony.core.service.LogService;
import net.dorokhov.pony.core.test.AbstractIntegrationCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class LogServiceIT extends AbstractIntegrationCase {

	private LogService service;

	@Before
	public void setUp() throws Exception {
		service = context.getBean(LogService.class);
	}

	@Test
	public void test() {

		// Debug

		checkMessageWithDetails(service.debug("test1"),
				LogMessage.Type.DEBUG, "test1", new ArrayList<String>(), null);
		checkMessageWithDetails(service.debug("test2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.DEBUG, "test2", Arrays.asList("arg1", "arg2"), null);

		checkMessageWithDetailsNotNull(service.debug("test3", new Exception()),
				LogMessage.Type.DEBUG, "test3", new ArrayList<String>());
		checkMessageWithDetailsNotNull(service.debug("test4", new Exception(), Arrays.asList("arg1", "arg2")),
				LogMessage.Type.DEBUG, "test4", Arrays.asList("arg1", "arg2"));

		checkMessageWithDetails(service.debug("test5", "details1"),
				LogMessage.Type.DEBUG, "test5", new ArrayList<String>(), "details1");
		checkMessageWithDetails(service.debug("test6", "details2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.DEBUG, "test6", Arrays.asList("arg1", "arg2"), "details2");

		// Info

		checkMessageWithDetails(service.info("test1"),
				LogMessage.Type.INFO, "test1", new ArrayList<String>(), null);
		checkMessageWithDetails(service.info("test2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.INFO, "test2", Arrays.asList("arg1", "arg2"), null);

		checkMessageWithDetailsNotNull(service.info("test3", new Exception()),
				LogMessage.Type.INFO, "test3", new ArrayList<String>());
		checkMessageWithDetailsNotNull(service.info("test4", new Exception(), Arrays.asList("arg1", "arg2")),
				LogMessage.Type.INFO, "test4", Arrays.asList("arg1", "arg2"));

		checkMessageWithDetails(service.info("test5", "details1"),
				LogMessage.Type.INFO, "test5", new ArrayList<String>(), "details1");
		checkMessageWithDetails(service.info("test6", "details2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.INFO, "test6", Arrays.asList("arg1", "arg2"), "details2");

		// Warn

		checkMessageWithDetails(service.warn("test1"),
				LogMessage.Type.WARN, "test1", new ArrayList<String>(), null);
		checkMessageWithDetails(service.warn("test2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.WARN, "test2", Arrays.asList("arg1", "arg2"), null);

		checkMessageWithDetailsNotNull(service.warn("test3", new Exception()),
				LogMessage.Type.WARN, "test3", new ArrayList<String>());
		checkMessageWithDetailsNotNull(service.warn("test4", new Exception(), Arrays.asList("arg1", "arg2")),
				LogMessage.Type.WARN, "test4", Arrays.asList("arg1", "arg2"));

		checkMessageWithDetails(service.warn("test5", "details1"),
				LogMessage.Type.WARN, "test5", new ArrayList<String>(), "details1");
		checkMessageWithDetails(service.warn("test6", "details2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.WARN, "test6", Arrays.asList("arg1", "arg2"), "details2");

		// Error

		checkMessageWithDetails(service.error("test1"),
				LogMessage.Type.ERROR, "test1", new ArrayList<String>(), null);
		checkMessageWithDetails(service.error("test2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.ERROR, "test2", Arrays.asList("arg1", "arg2"), null);

		checkMessageWithDetailsNotNull(service.error("test3", new Exception()),
				LogMessage.Type.ERROR, "test3", new ArrayList<String>());
		checkMessageWithDetailsNotNull(service.error("test4", new Exception(), Arrays.asList("arg1", "arg2")),
				LogMessage.Type.ERROR, "test4", Arrays.asList("arg1", "arg2"));

		checkMessageWithDetails(service.error("test5", "details1"),
				LogMessage.Type.ERROR, "test5", new ArrayList<String>(), "details1");
		checkMessageWithDetails(service.error("test6", "details2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.ERROR, "test6", Arrays.asList("arg1", "arg2"), "details2");

		// List

		Assert.assertEquals(6, service.getByType(LogMessage.Type.ERROR, new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(12, service.getByType(LogMessage.Type.WARN, new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(18, service.getByType(LogMessage.Type.INFO, new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(24, service.getByType(LogMessage.Type.DEBUG, new PageRequest(0, 100)).getTotalElements());

		long DAY_IN_MS = 1000 * 60 * 60 * 24;

		Assert.assertEquals(6, service.getByTypeAndDate(LogMessage.Type.ERROR, new Date(System.currentTimeMillis() - DAY_IN_MS), new Date(), new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(12, service.getByTypeAndDate(LogMessage.Type.WARN, new Date(System.currentTimeMillis() - DAY_IN_MS), new Date(), new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(18, service.getByTypeAndDate(LogMessage.Type.INFO, new Date(System.currentTimeMillis() - DAY_IN_MS), new Date(), new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(24, service.getByTypeAndDate(LogMessage.Type.DEBUG, new Date(System.currentTimeMillis() - DAY_IN_MS), new Date(), new PageRequest(0, 100)).getTotalElements());
	}

	private void checkMessageWithDetails(LogMessage aMessage, LogMessage.Type aType, String aCode, List<String> aArguments, String aDetails) {

		checkMessage(aMessage, aType, aCode, aArguments);

		Assert.assertEquals(aDetails, aMessage.getDetails());
	}

	private void checkMessageWithDetailsNotNull(LogMessage aMessage, LogMessage.Type aType, String aCode, List<String> aArguments) {

		checkMessage(aMessage, aType, aCode, aArguments);

		Assert.assertNotNull(aMessage.getDetails());
	}

	private void checkMessage(LogMessage aMessage, LogMessage.Type aType, String aCode, List<String> aArguments) {

		Assert.assertNotNull(aMessage.getId());
		Assert.assertNotNull(aMessage.getDate());

		Assert.assertEquals(aType, aMessage.getType());
		Assert.assertEquals(aCode, aMessage.getCode());
		Assert.assertEquals(aArguments, aMessage.getArguments());
	}
}
