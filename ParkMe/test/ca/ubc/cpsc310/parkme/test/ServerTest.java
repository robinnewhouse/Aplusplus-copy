package ca.ubc.cpsc310.parkme.test;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import junit.framework.TestCase;

import org.junit.Test;

public class ServerTest extends TestCase {

	private static final PersistenceManagerFactory PMF = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	PersistenceManager pm = PMF.getPersistenceManager();

	public void testAddTickets() {
		// TicketServiceImpl tsi = new TicketServiceImpl();
		// TicketInfo ti = new TicketInfo();
		// final TicketServiceAsync ticketService = GWT
		// .create(TicketService.class);
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
