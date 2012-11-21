package ca.ubc.cpsc310.parkme.server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;


import ca.ubc.cpsc310.parkme.client.services.parking.Fave;
import ca.ubc.cpsc310.parkme.client.services.parking.ParkingStats;
import ca.ubc.cpsc310.parkme.client.services.user.NotLoggedInException;

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

	public ParkingStats[] getMostFaved() {
		PersistenceManager pm = getPersistenceManager();
		ParkingStats[] faves;
		try {
			Query q = pm.newQuery(ParkingFave.class);
			
			q.setGrouping("parkingID");
			//q.setOrdering("count(user) desc");
			q.setResult("count(user) as count, parkingID");
			q.setResultClass(HashMap.class); 
			Collection results = (Collection) q.execute();
			//List<HashMap> results = (List<HashMap>) q.execute();
			int size = results.size();
			int i = 0;
			faves = new ParkingStats[size];
			
			//for (int i = 0; i < size; i++) {
			Iterator iter = results.iterator();
			while (iter.hasNext()) {
				Map row = (Map) iter.next();
				//Map row = (Map) results.get(i);
				String parkingID = (String) row.get("parkingID");
				Long count = (Long) row.get("count");
				faves[i] = new ParkingStats(parkingID, count);
				i++;
				
			}
		} finally {
			pm.close();
		}
		
		return faves;
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

	public boolean checkFave(String parkingID) throws NotLoggedInException {
		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		boolean faved;
		try {
			Query q = pm.newQuery(ParkingFave.class, "user == u && parkingID == pid");
			q.declareParameters("com.google.appengine.api.users.User u, String pid");

			List<ParkingFave> pFave = (List<ParkingFave>) q.execute(getUser(), parkingID);
			int size = pFave.size();
			if (size == 0) {
				faved = false;
			}
			else  {
				faved = true;
			}
		}	finally {
			pm.close();
		}
		return faved;
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

	public void removeFave(String parkingID) throws  NotLoggedInException {

		checkLoggedIn();
		PersistenceManager pm = getPersistenceManager();
		try {
			Query q = pm.newQuery(ParkingFave.class, "user == u && parkingID == pid");
			q.declareParameters("com.google.appengine.api.users.User u, String pid");

			List<ParkingFave> pFave = (List<ParkingFave>) q.execute(getUser(), parkingID);
			int size = pFave.size();
			if (size == 0) {
				System.out.println("error deleting fave");
			} else {
				for (ParkingFave parking : pFave) {
					pm.deletePersistent(parking);
					System.out.println("Deleted " + parkingID + " from favorites.");
				}
			}

		} finally {
			pm.close();
		}
	}
}
