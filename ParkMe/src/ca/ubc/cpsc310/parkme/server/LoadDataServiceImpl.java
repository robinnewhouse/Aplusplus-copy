package ca.ubc.cpsc310.parkme.server;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ca.ubc.cpsc310.parkme.client.LoadDataService;
import ca.ubc.cpsc310.parkme.client.ParkingLocation;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoadDataServiceImpl extends RemoteServiceServlet implements
LoadDataService {

	private static final PersistenceManagerFactory PMF = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	private static List<ParkingLoc> parking;

	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

	public void loadData() {
		PersistenceManager pm = getPersistenceManager();
		try {

			ParkingLocHandler kmlParser = new ParkingLocHandler();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			InputStream is = null;

			try {
				SAXParser saxParser = factory.newSAXParser();
				URL linkURL = new URL("http://data.vancouver.ca/download/kml/parking_meter_rates_and_time_limits.kmz");
				URLConnection urlConn = linkURL.openConnection();

				is = urlConn.getInputStream();

				ZipInputStream zis = new ZipInputStream(is);
				ZipEntry entry = zis.getNextEntry();
				while (entry != null && !entry.getName().endsWith("kml")) {
					entry = zis.getNextEntry();
				}
				if (entry == null) {
					throw new Exception("No KML file found in the KMZ package");
				}
				is = zis;

				saxParser.parse(is, kmlParser);

			} catch (Throwable err) {
				;
			}

			parking = kmlParser.getParkingLocList();
			if (parking != null) {
				pm.makePersistentAll(parking);
			}

		} finally {
			pm.close();
		}
	}

	public void setStreet(String street, String ID) {
		PersistenceManager pm = getPersistenceManager();
		try {
			ParkingLoc parkingLoc = pm.getObjectById(ParkingLoc.class, ID);
			parkingLoc.setStreet(street);
		} finally {
			pm.close();
		}
	}

	public ParkingLocation[] getParking() {
		PersistenceManager pm = getPersistenceManager();
		ParkingLocation[] parkingLocArray;
		try {
			Query q = pm.newQuery(ParkingLoc.class);
			List<ParkingLoc> parkingLocs = (List<ParkingLoc>) q.execute();
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

	public ParkingLocation[] getUnknownStreets() {
		PersistenceManager pm = getPersistenceManager();
		ParkingLocation[] parkingLocArray;
		try {
			Query q = pm.newQuery(ParkingLoc.class);
			q.setFilter("street == 'Vancouver'");
			List<ParkingLoc> parkingLocs = (List<ParkingLoc>) q.execute();
			int size = parkingLocs.size();
			System.out.println(size);
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
