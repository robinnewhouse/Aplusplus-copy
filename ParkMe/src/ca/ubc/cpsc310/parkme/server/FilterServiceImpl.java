package ca.ubc.cpsc310.parkme.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import ca.ubc.cpsc310.parkme.client.FilterService;
import ca.ubc.cpsc310.parkme.client.ParkingLocation;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FilterServiceImpl extends RemoteServiceServlet implements FilterService{

	private static final PersistenceManagerFactory PMF = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

	public ParkingLocation[] getParking() {
		PersistenceManager pm = getPersistenceManager();
		ParkingLocation[] parkingLocArray;
		try {
			Query q = pm.newQuery(ParkingLoc.class, "price <= 2.00");
			
			List<ParkingLoc> parkingLocs = (List<ParkingLoc>) q.execute();
			int size = parkingLocs.size();
			
			parkingLocArray = new ParkingLocation[size];
			
			for (int i=0; i<size; i++) {
				String parkingID = parkingLocs.get(i).getParkingID();
				double price = parkingLocs.get(i).getPrice();
				double limit = parkingLocs.get(i).getLimit();
				double startLat = parkingLocs.get(i).getStartLat();
				double startLong = parkingLocs.get(i).getStartLong();
				double endLat = parkingLocs.get(i).getEndLat();
				double endLong = parkingLocs.get(i).getEndLong();

				parkingLocArray[i] = new ParkingLocation(parkingID, price, limit, startLat, startLong, endLat, endLong);
			}
		} finally {
			pm.close();
		}

		return (ParkingLocation[]) parkingLocArray;
	}

}