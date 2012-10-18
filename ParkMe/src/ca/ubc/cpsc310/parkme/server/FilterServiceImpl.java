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

public class FilterServiceImpl extends RemoteServiceServlet implements FilterService{

	private static final PersistenceManagerFactory PMF = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

	public ParkingLocation[] getParking(Criteria crit) {
		PersistenceManager pm = getPersistenceManager();
		ParkingLocation[] parkingLocArray;
		try {
			/**
			Query priceQuery = pm.newQuery(ParkingLoc.class);
			priceQuery.setResult("priceResult");
			priceQuery.setFilter("price <= maxPrice");
			
			Query timeQuery = pm.newQuery(ParkingLoc.class);
			timeQuery.setResult("timeResult");
			timeQuery.setFilter("limit >= minTime");
			**/
			
			Query q = pm.newQuery(ParkingLoc.class);
			// LOOK AT GQL
			q.setFilter("price <= maxPrice");
			//q.setFilter("limit >= minTime");
			q.declareParameters("double maxPrice");
	
	
			List<ParkingLoc> parkingLocsPrice = (List<ParkingLoc>) q.execute(crit.getMaxPrice());
			
			System.out.println("Total parking with matching price: " + parkingLocsPrice.size());
			
			double minTime = crit.getMinTime();
			List<ParkingLoc> parkingLocs = new ArrayList<ParkingLoc>();
			for (ParkingLoc p : parkingLocsPrice) {
				if (p.getLimit() >= minTime) {
					parkingLocs.add(p);
				}
			}
			
			int size = parkingLocs.size();
			
			System.out.println("Total parking with matching price and limit: " + size);
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