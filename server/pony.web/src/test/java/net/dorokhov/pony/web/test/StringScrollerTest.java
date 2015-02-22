package net.dorokhov.pony.web.test;

import net.dorokhov.pony.web.client.service.StringScroller;
import org.junit.Assert;
import org.junit.Test;

public class StringScrollerTest {

	@Test
	public void testScrolling() {

		StringScroller scroller = new StringScroller("Pony - Foo Song12 | ");

		Assert.assertEquals("Pony - Foo Song12 | ", scroller.getResult());

		scroller.setOffset(0.05);

		Assert.assertEquals(0.05, scroller.getOffset(), 0.001);
		Assert.assertEquals(0.05, scroller.getNormalizedOffset(), 0.001);

		Assert.assertEquals("ony - Foo Song12 | P", scroller.getResult());

		scroller.setOffset(-0.95);

		Assert.assertEquals(-0.95, scroller.getOffset(), 0.001);
		Assert.assertEquals(0.05, scroller.getNormalizedOffset(), 0.001);

		Assert.assertEquals("ony - Foo Song12 | P", scroller.getResult());

		scroller.setOffset(0.5);

		Assert.assertEquals(0.5, scroller.getOffset(), 0.001);
		Assert.assertEquals(0.5, scroller.getNormalizedOffset(), 0.001);

		Assert.assertEquals(" Song12 | Pony - Foo", scroller.getResult());

		scroller.setOffset(1.5);

		Assert.assertEquals(1.5, scroller.getOffset(), 0.001);
		Assert.assertEquals(0.5, scroller.getNormalizedOffset(), 0.001);

		Assert.assertEquals(" Song12 | Pony - Foo", scroller.getResult());
	}

}
