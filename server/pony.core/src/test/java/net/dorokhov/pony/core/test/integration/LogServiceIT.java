package net.dorokhov.pony.core.test.integration;

import net.dorokhov.pony.core.entity.LogMessage;
import net.dorokhov.pony.core.logging.LogService;
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
		service.deleteAll();
	}

	@Test
	public void test() {

		// Debug

		checkMessageWithDetails(service.debug("test1", "text1"),
				LogMessage.Type.DEBUG, "test1", "text1", new ArrayList<String>(), null);
		checkMessageWithDetails(service.debug("test2", "text2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.DEBUG, "test2", "text2", Arrays.asList("arg1", "arg2"), null);

		checkMessageWithDetailsNotNull(service.debug("test3", "text3", new Exception()),
				LogMessage.Type.DEBUG, "test3", "text3", new ArrayList<String>());
		checkMessageWithDetailsNotNull(service.debug("test4", "text4", new Exception(), Arrays.asList("arg1", "arg2")),
				LogMessage.Type.DEBUG, "test4", "text4", Arrays.asList("arg1", "arg2"));

		checkMessageWithDetails(service.debug("test5", "text5", "details1"),
				LogMessage.Type.DEBUG, "test5", "text5", new ArrayList<String>(), "details1");
		checkMessageWithDetails(service.debug("test6", "text6", "details2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.DEBUG, "test6", "text6", Arrays.asList("arg1", "arg2"), "details2");

		// Info

		checkMessageWithDetails(service.info("test1", "text1"),
				LogMessage.Type.INFO, "test1", "text1", new ArrayList<String>(), null);
		checkMessageWithDetails(service.info("test2", "text2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.INFO, "test2", "text2", Arrays.asList("arg1", "arg2"), null);

		checkMessageWithDetailsNotNull(service.info("test3", "text3", new Exception()),
				LogMessage.Type.INFO, "test3", "text3", new ArrayList<String>());
		checkMessageWithDetailsNotNull(service.info("test4", "text4", new Exception(), Arrays.asList("arg1", "arg2")),
				LogMessage.Type.INFO, "test4", "text4", Arrays.asList("arg1", "arg2"));

		checkMessageWithDetails(service.info("test5", "text5", "details1"),
				LogMessage.Type.INFO, "test5", "text5", new ArrayList<String>(), "details1");
		checkMessageWithDetails(service.info("test6", "text6", "details2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.INFO, "test6", "text6", Arrays.asList("arg1", "arg2"), "details2");

		// Warn

		checkMessageWithDetails(service.warn("test1", "text1"),
				LogMessage.Type.WARN, "test1", "text1", new ArrayList<String>(), null);
		checkMessageWithDetails(service.warn("test2", "text2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.WARN, "test2", "text2", Arrays.asList("arg1", "arg2"), null);

		checkMessageWithDetailsNotNull(service.warn("test3", "text3", new Exception()),
				LogMessage.Type.WARN, "test3", "text3", new ArrayList<String>());
		checkMessageWithDetailsNotNull(service.warn("test4", "text4", new Exception(), Arrays.asList("arg1", "arg2")),
				LogMessage.Type.WARN, "test4", "text4", Arrays.asList("arg1", "arg2"));

		checkMessageWithDetails(service.warn("test5", "text5", "details1"),
				LogMessage.Type.WARN, "test5", "text5", new ArrayList<String>(), "details1");
		checkMessageWithDetails(service.warn("test6", "text6", "details2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.WARN, "test6", "text6", Arrays.asList("arg1", "arg2"), "details2");

		// Error

		checkMessageWithDetails(service.error("test1", "text1"),
				LogMessage.Type.ERROR, "test1", "text1", new ArrayList<String>(), null);
		checkMessageWithDetails(service.error("test2", "text2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.ERROR, "test2", "text2", Arrays.asList("arg1", "arg2"), null);

		checkMessageWithDetailsNotNull(service.error("test3", "text3", new Exception()),
				LogMessage.Type.ERROR, "test3", "text3", new ArrayList<String>());
		checkMessageWithDetailsNotNull(service.error("test4", "text4", new Exception(), Arrays.asList("arg1", "arg2")),
				LogMessage.Type.ERROR, "test4", "text4", Arrays.asList("arg1", "arg2"));

		checkMessageWithDetails(service.error("test5", "text5", "details1"),
				LogMessage.Type.ERROR, "test5", "text5", new ArrayList<String>(), "details1");
		checkMessageWithDetails(service.error("test6", "text6", "details2", Arrays.asList("arg1", "arg2")),
				LogMessage.Type.ERROR, "test6", "text6", Arrays.asList("arg1", "arg2"), "details2");

		// Listing

		Assert.assertEquals(6, service.getByType(LogMessage.Type.ERROR, new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(12, service.getByType(LogMessage.Type.WARN, new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(18, service.getByType(LogMessage.Type.INFO, new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(24, service.getByType(LogMessage.Type.DEBUG, new PageRequest(0, 100)).getTotalElements());

		long DAY_IN_MS = 1000 * 60 * 60 * 24;

		Assert.assertEquals(6, service.getByTypeAndDate(LogMessage.Type.ERROR, new Date(System.currentTimeMillis() - DAY_IN_MS), new Date(), new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(12, service.getByTypeAndDate(LogMessage.Type.WARN, new Date(System.currentTimeMillis() - DAY_IN_MS), new Date(), new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(18, service.getByTypeAndDate(LogMessage.Type.INFO, new Date(System.currentTimeMillis() - DAY_IN_MS), new Date(), new PageRequest(0, 100)).getTotalElements());
		Assert.assertEquals(24, service.getByTypeAndDate(LogMessage.Type.DEBUG, new Date(System.currentTimeMillis() - DAY_IN_MS), new Date(), new PageRequest(0, 100)).getTotalElements());

		// Deletion

		Assert.assertEquals(24, service.getCount());

		service.deleteAll();

		Assert.assertEquals(0, service.getCount());
	}

	private void checkMessageWithDetails(LogMessage aMessage, LogMessage.Type aType, String aCode, String aText, List<String> aArguments, String aDetails) {

		checkMessage(aMessage, aType, aCode, aText, aArguments);

		Assert.assertEquals(aDetails, aMessage.getDetails());
	}

	private void checkMessageWithDetailsNotNull(LogMessage aMessage, LogMessage.Type aType, String aCode, String aText, List<String> aArguments) {

		checkMessage(aMessage, aType, aCode, aText, aArguments);

		Assert.assertNotNull(aMessage.getDetails());
	}

	private void checkMessage(LogMessage aMessage, LogMessage.Type aType, String aCode, String aText, List<String> aArguments) {

		Assert.assertNotNull(aMessage.getId());
		Assert.assertNotNull(aMessage.getDate());

		Assert.assertEquals(aType, aMessage.getType());
		Assert.assertEquals(aCode, aMessage.getCode());
		Assert.assertEquals(aText, aMessage.getText());
		Assert.assertEquals(aArguments, aMessage.getArguments());
	}
}
