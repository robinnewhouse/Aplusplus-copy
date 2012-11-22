package ca.ubc.cpsc310.parkme.client;

import ca.ubc.cpsc310.parkme.client.services.parking.TicketInfo;

import com.google.gwt.junit.client.GWTTestCase;

public class ClientTest extends GWTTestCase {

	public TicketInfo[] ticks;

	@Override
	public String getModuleName() {
		return "ca.ubc.cpsc310.parkme.ParkMe";

	}

	public void testSimple() {
		assertTrue(true);
	}
}
