package ca.ubc.cpsc310.parkme.server;

import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ca.ubc.cpsc310.parkme.client.services.parking.LoadDataService;
import ca.ubc.cpsc310.parkme.client.services.parking.ParkingLocService;
import ca.ubc.cpsc310.parkme.client.services.parking.ParkingLocation;
import ca.ubc.cpsc310.parkme.client.services.user.Criteria;

public class ParkingLocServiceImpl extends RemoteServiceServlet implements ParkingLocService {

	private static final PersistenceManagerFactory PMF = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

	public ParkingLocation getParking(String id) {
		PersistenceManager pm = getPersistenceManager();
		ParkingLocation parkingLoc;
		try {
			Query q = pm.newQuery(ParkingLoc.class);
			q.setFilter("parkingID == pID");
			q.declareParameters("String pID");
			List<ParkingLoc> parkingLocsPrice = (List<ParkingLoc>) q.execute(id);
			ParkingLoc parking = parkingLocsPrice.get(0);
			String parkingID = parking.getParkingID();
			double price = parking.getPrice();
			double limit = parking.getLimit();
			double startLat = parking.getStartLat();
			double startLong = parking.getStartLong();
			double endLat = parking.getEndLat();
			double endLong = parking.getEndLong();
			String street = parking.getStreet();
			String color = parking.getColor();
			
			parkingLoc = new ParkingLocation(parkingID, price,
					limit, startLat, startLong, endLat, endLong, street, color);

		} finally {
			pm.close();
		}

		return (ParkingLocation) parkingLoc;
	}

	public ParkingLocation[] getParkings(String[] ids) {
		int size = ids.length;
		ParkingLocation[] parkingLoc = new ParkingLocation[size];
		for (int i = 0; i < size; i++) {
			parkingLoc[i] = getParking(ids[i]);
		}
		return parkingLoc;
	}
}
