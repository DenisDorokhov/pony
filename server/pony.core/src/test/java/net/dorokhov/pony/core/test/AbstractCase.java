package net.dorokhov.pony.core.test;

import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractCase {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Before
	public void baseSetUp() throws Exception {

	}

	@After
	public void baseTearDown() throws Exception {

	}
}
