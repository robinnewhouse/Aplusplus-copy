package ca.ubc.cpsc310.parkme.test;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import junit.framework.TestCase;

import org.junit.Test;

import ca.ubc.cpsc310.parkme.server.TicketServiceImpl;

public class ServerTest extends TestCase {

	private static final PersistenceManagerFactory PMF = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	PersistenceManager pm = PMF.getPersistenceManager();
	TicketServiceImpl tsi = new TicketServiceImpl();

	public void testAddTickets() {
		// try {
		// pm.addTicket("id-1", 100.0);
		// } catch (NotLoggedInException e) {
		// e.printStackTrace();
		// }
	}

	@Test
	public void testSimple() { // (3)
		assertTrue(true);
	}

	// Ticket Testing
}
