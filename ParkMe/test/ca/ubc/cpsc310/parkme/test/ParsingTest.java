package ca.ubc.cpsc310.parkme.test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;

import org.junit.Test;
import org.xml.sax.SAXException;

import ca.ubc.cpsc310.parkme.server.ParkingLoc;
import ca.ubc.cpsc310.parkme.server.ParkingLocHandler;
//import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
//import com.google.appengine.tools.development.testing.LocalServiceTestHelper;

public class ParsingTest extends TestCase {

	InputStream inputStream;
	ParkingLocHandler kmlParser;
	SAXParserFactory factory;
	SAXParser saxParser;
	List<ParkingLoc> pll;

	// QUESTION: this will not run before my tests. to not have to parse the
	// data
	// every time, i knidof need it to.
	// there is an issue on stackoverflow regarding this, what should i do?
	// http://stackoverflow.com/questions/733037/why-isnt-my-beforeclass-method-running

	public void parseTestXml() {
		inputStream = this.getClass().getResourceAsStream("TestXml.kml");

		try {
			if (inputStream.available() > 0) {
				assertTrue(true);
			} else {
				System.out.println("file is empty");
				assertTrue(false);
			}
		} catch (IOException e) {
			System.out.println("IOException in testKmlParser");
			assertTrue(false);
			e.printStackTrace();
		}
		// Setup for parsing
		kmlParser = new ParkingLocHandler();
		factory = SAXParserFactory.newInstance();
		try {
			saxParser = factory.newSAXParser();
			saxParser.parse(inputStream, kmlParser);
		} catch (ParserConfigurationException e) {
			System.out.println("ParserConfigurationException in testKmlParser");
			assertTrue(false);
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println("SAXException in testKmlParser");
			assertTrue(false);
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException in testKmlParser");
			assertTrue(false);
			e.printStackTrace();
		}

		pll = kmlParser.getParkingLocList();

	}

	/**
	 * Must refer to a valid module that sources this class.
	 */
	public String getModuleName() { // (2)
		return "ca.ubc.cpsc310.parkme.ParkMe";
	}

	@Test
	public void testSimple() { // (3)
		assertTrue(true);
	}

	// You need to make sure data is parsed as intended
	@Test
	public void testKmlData() {

		parseTestXml();

		List<ParkingLoc> pll = kmlParser.getParkingLocList();

		ParkingLoc pl0 = pll.get(0);
		ParkingLoc pl1 = pll.get(1);

		assertEquals(49.2630169018194, pl0.getStartLat(), 0.0000000000001);
		assertEquals(-123.100739208611, pl0.getStartLong(), 0.0000000000001);
		assertEquals(49.2630078055425, pl0.getEndLat(), 0.0000000000001);
		assertEquals(-123.100348847572, pl0.getEndLong(), 0.0000000000001);
		assertEquals(2.00, pl0.getLimit());
		assertEquals(1.00, pl0.getPrice());
		assertEquals("kml_1", pl0.getParkingID());

		assertEquals(49.2627730507724, pl1.getStartLat(), 0.0000000000001);
		assertEquals(-123.100756446135, pl1.getStartLong(), 0.0000000000001);
		assertEquals(49.2627639545521, pl1.getEndLat(), 0.0000000000001);
		assertEquals(-123.100366087016, pl1.getEndLong(), 0.0000000000001);
		assertEquals(2.00, pl1.getLimit());
		assertEquals(1.00, pl1.getPrice());
		assertEquals("kml_2", pl1.getParkingID());

		assertTrue(true);
	}

	// check for colors
	@Test
	public void testColorCalculation() {
		parseTestXml();

		List<ParkingLoc> pll = kmlParser.getParkingLocList();

		ParkingLoc pl100 = findParkingLocByPrice(pll, 1.00);
		ParkingLoc pl150 = findParkingLocByPrice(pll, 1.50);
		ParkingLoc pl200 = findParkingLocByPrice(pll, 2.00);
		ParkingLoc pl250 = findParkingLocByPrice(pll, 2.50);
		ParkingLoc pl300 = findParkingLocByPrice(pll, 3.00);
		ParkingLoc pl350 = findParkingLocByPrice(pll, 3.50);
		ParkingLoc pl400 = findParkingLocByPrice(pll, 4.00);
		ParkingLoc pl450 = findParkingLocByPrice(pll, 4.50);
		ParkingLoc pl500 = findParkingLocByPrice(pll, 5.00);
		ParkingLoc pl10000 = findParkingLocByPrice(pll, 100.00);
		ParkingLoc plneg1 = findParkingLocByPrice(pll, -1.00);

		assertEquals(pl100.getColor(), "#66CD00");
		assertEquals(pl150.getColor(), "#9BD500");
		assertEquals(pl200.getColor(), "#B7D900");
		assertEquals(pl250.getColor(), "#E0CF00");
		assertEquals(pl300.getColor(), "#E8A100");
		assertEquals(pl350.getColor(), "#EC8800");
		assertEquals(pl400.getColor(), "#F35400");
		assertEquals(pl450.getColor(), "#FB1D00");
		assertEquals(pl500.getColor(), "#FF0000");
		assertEquals(pl10000.getColor(), "#FF0000");
		assertEquals(plneg1.getColor(), "#66CD00");

		assertTrue(true);
	}

	/**
	 * test that kml is retrievable from kmz code is taken from
	 * LoadDataServiceImpl loadData() except for using a local kmz
	 */
	public void testRetrievalOfKmlFromKmz() {
		InputStream is = null;
		try {
			URL linkURL = new URL(
					"http://data.vancouver.ca/download/kml/parking_meter_rates_and_time_limits.kmz");
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

			// what is returned from the particular data we're using
			assertEquals(is.read(), 60);

		} catch (Throwable e) {
			e.printStackTrace();
			assertTrue(false);
		}

	}

	// A helper method to traverse the list of parking locs
	public ParkingLoc findParkingLocByPrice(List<ParkingLoc> pll, double price) {
		for (ParkingLoc parkingLoc : pll) {
			if (parkingLoc.getPrice() == price)
				return parkingLoc;
		}
		return null;
	}

}