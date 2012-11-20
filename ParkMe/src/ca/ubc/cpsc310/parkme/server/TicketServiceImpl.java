package ca.ubc.cpsc310.parkme.server;

import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import ca.ubc.cpsc310.parkme.client.NotLoggedInException;
import ca.ubc.cpsc310.parkme.client.TicketInfo;
import ca.ubc.cpsc310.parkme.client.TicketService;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TicketServiceImpl extends RemoteServiceServlet implements
		TicketService {
	private static final PersistenceManagerFactory PMF = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	public void addTicket(String parkingID, Double fine)
			throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(new Ticket(getUser(), parkingID, fine));
		} finally {
			pm.close();
		}
	}

	public TicketInfo[] getTickets() throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		TicketInfo[] tickets;
		try {
			Query q = pm.newQuery(ParkingFave.class, "user == u");
			q.declareParameters("com.google.appengine.api.users.User u");
			q.setOrdering("createDate");
			List<Ticket> pTicket = (List<Ticket>) q.execute(getUser());

			int size = pTicket.size();
			tickets = new TicketInfo[size];
			for (int i = 0; i < size; i++) {
				tickets[i] = new TicketInfo(pTicket.get(i).getParkingID(),
						pTicket.get(i).getFine());
			}
		} finally {
			pm.close();
		}
		return tickets;
	}

	private void checkLoggedIn() throws NotLoggedInException {
		if (getUser() == null) {
			throw new NotLoggedInException("Not logged in.");
		}
	}

	private User getUser() {
		UserService userService = UserServiceFactory.getUserService();
		return userService.getCurrentUser();
	}

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

}
