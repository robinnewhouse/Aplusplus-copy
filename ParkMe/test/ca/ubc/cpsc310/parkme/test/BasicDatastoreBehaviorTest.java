package ca.ubc.cpsc310.parkme.test;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withLimit;

import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import junit.framework.TestCase;

import org.junit.Test;

import ca.ubc.cpsc310.parkme.server.ParkingLoc;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;

public class BasicDatastoreBehaviorTest extends TestCase {

	private static final PersistenceManagerFactory PMF = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	PersistenceManager pm = PMF.getPersistenceManager();

	@Test
	public void testSimple() { // (3)
		assertTrue(true);
	}

	// run this test twice to prove we're not leaking any state across tests
	private void doTest() throws Exception {
		setUp();
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		assertEquals(0,
				ds.prepare(new Query("yam")).countEntities(withLimit(10)));

		ds.put(new Entity("yam"));
		ds.put(new Entity("yam"));

		assertEquals(2,
				ds.prepare(new Query("yam")).countEntities(withLimit(10)));
		tearDown();
	}

	@Test
	public void testInsert1() {
		try {
			doTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testInsert2() {
		try {
			doTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// and have the same attributes as before.

	// A helper method to traverse the list of parking locs
	public ParkingLoc findParkingLocByPrice(List<ParkingLoc> pll, double price) {
		for (ParkingLoc parkingLoc : pll) {
			if (parkingLoc.getPrice() == price)
				return parkingLoc;
		}
		return null;
	}
}
