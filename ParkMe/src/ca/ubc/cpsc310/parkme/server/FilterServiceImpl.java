package ca.ubc.cpsc310.parkme.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import ca.ubc.cpsc310.parkme.client.Criteria;
import ca.ubc.cpsc310.parkme.client.FilterService;
import ca.ubc.cpsc310.parkme.client.ParkingLocation;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FilterServiceImpl extends RemoteServiceServlet implements
		FilterService {

	private static final PersistenceManagerFactory PMF = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

	public ParkingLocation[] getParking(Criteria crit) {

		PersistenceManager pm = getPersistenceManager();
		ParkingLocation[] parkingLocArray;
		try {
			Query q = pm.newQuery(ParkingLoc.class);

			// First find all parkings that are <= maxPrice
			q.setFilter("price <= maxPrice");
			q.declareParameters("double maxPrice");

			List<ParkingLoc> parkingLocsPrice = (List<ParkingLoc>) q
					.execute(crit.getMaxPrice());

			// Now filter through results to get those with >= minTime
			double minTime = crit.getMinTime();
			List<ParkingLoc> parkingLocs = new ArrayList<ParkingLoc>();
			for (ParkingLoc p : parkingLocsPrice) {
				if (p.getLimit() >= minTime) {
					parkingLocs.add(p);
				}
			}

			int size = parkingLocs.size();

			parkingLocArray = new ParkingLocation[size];

			for (int i = 0; i < size; i++) {
				String parkingID = parkingLocs.get(i).getParkingID();
				double price = parkingLocs.get(i).getPrice();
				double limit = parkingLocs.get(i).getLimit();
				double startLat = parkingLocs.get(i).getStartLat();
				double startLong = parkingLocs.get(i).getStartLong();
				double endLat = parkingLocs.get(i).getEndLat();
				double endLong = parkingLocs.get(i).getEndLong();
				String street = parkingLocs.get(i).getStreet();

				
				String color = "black";
				if (price < 2) {
					color = "#66CD00";
				} else if (price < 3 && price >= 2) {
					color = "#FFE303";
				} else if (price >= 3 && price < 4) {
					color = "#FF7F24";
				} else if (price >= 4) {
					color = "#FF0000";
				}
				
				parkingLocArray[i] = new ParkingLocation(parkingID, price,
						limit, startLat, startLong, endLat, endLong, street, color);
			}
		} finally {
			pm.close();
		}

		return (ParkingLocation[]) parkingLocArray;
	}

}