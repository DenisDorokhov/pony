package net.dorokhov.pony.core.test.unit;

import java.util.LinkedList;
import java.util.List;

import net.dorokhov.pony.core.common.SqlSplitter;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SqlSplitterTest {

	private SqlSplitter splitterWithDefaults;

	@Before
	public void setUp() {
		splitterWithDefaults = new SqlSplitter();
	}

	@Test
	public void test() {
		
		String sqlScript =
				"-- This is SQL comment with ;;; separator inside \n" +
				"INSERT INTO SomeTable (Name, Description) VALUES ('--Some name;;;--', 'Some; description');" + 
				"SELECT * FROM SomeTable WHERE ID = 100;;;" +
				"-- This is another SQL comment with ';;;' another separator \n" +
				"DELETE FROM SomeTable WHERE ID = 100;";

		LinkedList<String> expectedResult = new LinkedList<>();
		
		expectedResult.add("INSERT INTO SomeTable (Name, Description) VALUES ('--Some name;;;--', 'Some; description')");
		expectedResult.add("SELECT * FROM SomeTable WHERE ID = 100");
		expectedResult.add("DELETE FROM SomeTable WHERE ID = 100");
				
		List<String> result = splitterWithDefaults.splitScript(sqlScript);

		Assert.assertArrayEquals(expectedResult.toArray(), result.toArray());
	}
}
