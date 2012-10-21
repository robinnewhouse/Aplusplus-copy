package ca.ubc.cpsc310.parkme.server;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * @author Alyanna Uy
 * 
 */
public class ParkingLocHandler extends DefaultHandler {
	public static List<ParkingLoc> parking;
	private ParkingLoc currentParking;
	private final static Pattern coordPattern = Pattern
			.compile("([^,]+),([^,]+),0" + "([^,]+),([^,]+),0 "
					+ "(([^,]+),([^,]+),0)*" + "(([^,]+),([^,]+),0 )*");
	private boolean isRate;
	private boolean isLimit;
	private boolean isCoord;
	private Double currentStartLat;
	private Double currentStartLong;
	private Double currentEndLat;
	private Double currentEndLong;

	public ParkingLocHandler() {
		isRate = false;
		isLimit = false;
		isCoord = false;
	}

	@Override
	public void startDocument() throws SAXException {
		parking = new ArrayList<ParkingLoc>();
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

		if (qName.equalsIgnoreCase("Placemark")) {
			this.currentParking = new ParkingLoc();
			String parkingID = attributes.getValue("id");
			currentParking.setParkingID(parkingID);
		} else if (qName.equalsIgnoreCase("SimpleData")) {

			String attr = attributes.getValue("name");
			if (attr.equalsIgnoreCase("RATE")) {
				isRate = true;
			} else if (attr.equalsIgnoreCase("LIMIT")) {
				isLimit = true;
			}

		} else if (qName.equals("coordinates")) {
			isCoord = true;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

		if (this.currentParking != null) {
			if (qName.equalsIgnoreCase("Placemark")) {
				parking.add(currentParking);
			}
		}
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (isRate) {
			String price = new String(ch, start + 1, length - 1);
			Double rate = Double.parseDouble(price);
			currentParking.setPrice(rate);
			isRate = false;
		} else if (isLimit) {
			Double limit = Double
					.parseDouble(new String(ch, start, length - 2));
			currentParking.setLimit(limit);
			isLimit = false;
		}

		else if (isCoord) {
			String coord = (new String(ch, start, length));
			parseCoord(coord);
			currentParking.setStartLat(currentStartLat);
			currentParking.setStartLong(currentStartLong);
			currentParking.setEndLat(currentEndLat);
			currentParking.setEndLong(currentEndLong);
			currentParking.setStreet("Vancouver");
			isCoord = false;
			currentStartLat = 0.0;
			currentStartLong = 0.0;
			currentEndLat = 0.0;
			currentEndLong = 0.0;
		}
	}

	public void parseCoord(String location) {
		Matcher coord = coordPattern.matcher(location);
		if (coord.matches()) {
			currentStartLong = Double.parseDouble(coord.group(1));
			currentStartLat = Double.parseDouble(coord.group(2));
			currentEndLong = Double.parseDouble(coord.group(3));
			currentEndLat = Double.parseDouble(coord.group(4));
		}
	}

	public List<ParkingLoc> getParkingLocList() {
		return parking;
	}

}
