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

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoadDataServiceImpl  extends RemoteServiceServlet implements LoadDataService {

	private static final PersistenceManagerFactory PMF = JDOHelper
			.getPersistenceManagerFactory("transactions-optional");
	private static List<ParkingLoc> parking;


	private PersistenceManager getPersistenceManager() {
		return PMF.getPersistenceManager();
	}

	public void loadData() {
		PersistenceManager pm = getPersistenceManager();
		try {
		//	parseData();
			
			ParkingLocHandler kmlParser = new ParkingLocHandler();
			SAXParserFactory factory = SAXParserFactory.newInstance();
			InputStream is = null;

			try {
				SAXParser saxParser = factory.newSAXParser();
				URL linkURL = new URL("http://data.vancouver.ca/download/kml/parking_meter_rates_and_time_limits.kmz");

				URLConnection urlConn = linkURL.openConnection();
				String contentType = urlConn.getHeaderField("Content-Type");
				System.out.println("ContentType: " + contentType);

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

	public String[] getParking() {
		PersistenceManager pm = getPersistenceManager();
		List<String> parkingIDs = new ArrayList<String>();
		try {
			Query q = pm.newQuery("select from " + ParkingLoc.class.getName());
			
			List<ParkingLoc> parkingLocs = (List<ParkingLoc>) q.execute();
			for (ParkingLoc p : parkingLocs) {
				parkingIDs.add(p.getParkingID());
			}
		} finally {
			pm.close();
		}
		return (String[]) parkingIDs.toArray(new String[0]);
	}
	
/*	public void parseData() {
		ParkingLocHandler kmlParser = new ParkingLocHandler();
		SAXParserFactory factory = SAXParserFactory.newInstance();
		InputStream is = null;

		try {
			SAXParser saxParser = factory.newSAXParser();
			URL linkURL = new URL("http://data.vancouver.ca/download/kml/parking_meter_rates_and_time_limits.kmz");

			URLConnection urlConn = linkURL.openConnection();
			String contentType = urlConn.getHeaderField("Content-Type");
			System.out.println("ContentType: " + contentType);

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


	}*/

}
