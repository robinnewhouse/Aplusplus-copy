package ca.ubc.cpsc310.parkme.test;

import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import junit.framework.TestCase;

import org.junit.Test;

import ca.ubc.cpsc310.parkme.client.services.parking.TicketInfo;
import ca.ubc.cpsc310.parkme.server.Ticket;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.User;

public class ServerTest extends TestCase {

	private static final PersistenceManagerFactory PMF = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	PersistenceManager pm = PMF.getPersistenceManager();

	private void testAddTickets() {

		final PersistenceManagerFactory PMF = JDOHelper
				.getPersistenceManagerFactory("transactions-optional");
		PersistenceManager pm = PMF.getPersistenceManager();

		try {
			setUp();
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService();

			User user1 = new User("user1", "gmail.com");
			User user2 = new User("user2", "gmail.com");
			Ticket testTicket1 = new Ticket(user1, "id1", 100.0);
			Ticket testTicket2 = new Ticket(user1, "id2", 100.0);
			Ticket testTicket3 = new Ticket(user2, "id3", 100.0);

			TicketInfo[] tickets;
			try {
				javax.jdo.Query q = pm.newQuery(Ticket.class, "user == u");
				q.declareParameters("com.google.appengine.api.users.User u");
				q.setOrdering("createDate");
				List<Ticket> pTicket = (List<Ticket>) q.execute(user1);

				int size = pTicket.size();
				tickets = new TicketInfo[size];
				for (int i = 0; i < size; i++) {
					tickets[i] = new TicketInfo(pTicket.get(i).getParkingID(),
							pTicket.get(i).getFine());
				}
			} finally {
				pm.close();
			}

			tearDown();
		} catch (Exception e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	@Test
	public void testSimple() { // (3)
		assertTrue(true);
	}

	// Ticket Testing
}
