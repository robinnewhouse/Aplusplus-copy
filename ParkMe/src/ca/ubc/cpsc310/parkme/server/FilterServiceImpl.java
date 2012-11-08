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
	
	// Returns true if the endpoints or midpoint of the parking location are within radius metres of point
	private boolean isInRadius(ParkingLoc p, Double radius, double ctrLat, double ctrLng) {
		double startlat = p.getStartLat();
		double startlong = p.getStartLong();
		double endlat = p.getEndLat();
		double endlong = p.getEndLong();
		return (distance(startlat, startlong, ctrLat, ctrLng) <= radius
				|| distance(endlat, endlong, ctrLat, ctrLng) <= radius
				|| distance((startlat + endlat)/2, (startlong + endlong)/2, ctrLat, ctrLng) <= radius);
	}
	
	// Returns the distance between two points in metres, given their lats and longs
	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + 
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
		dist = Math.acos(dist);
		dist = Math.toDegrees(dist);
		dist = dist * 60 * 1151.5;
		dist = dist * 1.609344;
		//System.out.println(dist);
		return dist;
	}

	public ParkingLocation[] getParking(Criteria crit) {

		PersistenceManager pm = getPersistenceManager();
		ParkingLocation[] parkingLocArray;
		
		try {		
			System.out.println("About to filter results");
			Query q = pm.newQuery(ParkingLoc.class);

			// First find all parkings that are <= maxPrice
			q.setFilter("price <= maxPrice");
			q.declareParameters("double maxPrice");

			List<ParkingLoc> parkingLocsPrice = (List<ParkingLoc>) q.execute(crit.getMaxPrice());
			
			System.out.println("Have results filtered by price");
			System.out.println(parkingLocsPrice.size() + " results with matching price");

			// Now filter through results to get those with >= minTime and <= radius
			double minTime = crit.getMinTime();
			double radius = crit.getRadius();
			double ctrLat = crit.getCtrLat();
			double ctrLng = crit.getCtrLng();
			
			List<ParkingLoc> parkingLocs = new ArrayList<ParkingLoc>();
			
			// Filter without search location
			if (radius == 99999999) {
				for (ParkingLoc p : parkingLocsPrice) {
					if (p.getLimit() >= minTime) {
						parkingLocs.add(p);
						//System.out.println(p.getStreet() + " matches criteria");
					}
					//System.out.println(p.getStreet() + " does not match criteria");
				}
			} 
			// Filter with search location and radius
			else {
				for (ParkingLoc p : parkingLocsPrice) {
					if (p.getLimit() >= minTime && isInRadius(p, radius, ctrLat, ctrLng)) {
						parkingLocs.add(p);
						//System.out.println(p.getStreet() + " matches criteria");
					}
					//System.out.println(p.getStreet() + " does not match criteria");
				}
			}
			int size = parkingLocs.size();

			parkingLocArray = new ParkingLocation[size];

			for (int i = 0; i < size; i++) {

				parkingLocArray[i] = parkingLocs.get(i).convertToPL();
			}
		} finally {
			pm.close();
		}

		return (ParkingLocation[]) parkingLocArray;
	}

}