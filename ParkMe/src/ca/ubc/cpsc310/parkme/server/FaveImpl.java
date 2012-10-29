package ca.ubc.cpsc310.parkme.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import ca.ubc.cpsc310.parkme.client.Fave;
import ca.ubc.cpsc310.parkme.client.NotLoggedInException;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


public class FaveImpl extends RemoteServiceServlet implements Fave {
	private static final PersistenceManagerFactory PMF =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");

	public void addFave(String parkingID) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		try {
			pm.makePersistent(new ParkingFave(getUser(), parkingID));
		} finally {
			pm.close();
		}
	}


	public String[] getFaves() throws NotLoggedInException {

		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		String[] parkingIDs;
		try {
			Query q = pm.newQuery(ParkingFave.class, "user == u");
			q.declareParameters("com.google.appengine.api.users.User u");
			q.setOrdering("createDate");
			List<ParkingFave> pFave = (List<ParkingFave>) q.execute(getUser());
		
			int size = pFave.size();
			parkingIDs = new String[size];
			for (int i = 0; i < size; i++) {	
				parkingIDs[i] = (pFave.get(i).getParkingID());
			}
		} finally {
			pm.close();
		}
		return parkingIDs;
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
