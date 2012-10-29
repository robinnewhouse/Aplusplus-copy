package ca.ubc.cpsc310.parkme.test;

import com.google.gwt.junit.client.GWTTestCase;

/**
 * GWT JUnit tests must extend GWTTestCase.
 */
public class ParkMeTest extends GWTTestCase { // (1)

	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() { // (2)
		return "ca.ubc.cpsc310.parkme";
	}

	/**
	 * Add as many tests as you like.
	 */
	public void testSimple() { // (3)
		assertTrue(true);
	}

}