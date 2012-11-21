package ca.ubc.cpsc310.parkme.client;

import java.util.HashSet;
import java.util.Iterator;

import ca.ubc.cpsc310.parkme.client.services.parking.ParkingLocation;

import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.ui.Button;
import com.google.maps.gwt.client.Circle;
import com.google.maps.gwt.client.CircleOptions;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MouseEvent;
import com.google.maps.gwt.client.Polyline;
import com.google.maps.gwt.client.PolylineOptions;

public class MapOperater {

	private GoogleMap theMap;
	private HashSet<Polyline> polylines = new HashSet<Polyline>();
	private Circle circle = Circle.create();
	public Marker marker = Marker.create();
	LatLng DEFAULT_CENTER = LatLng.create(49.251, -123.119);

	public MapOperater(GoogleMap map) {
		this.theMap = map;
	}

	public void drawLocs(ParkingLocation[] lopl, MyInfoWindow infoWindow,
			Button addToFave, Button addTicket) {
		clearMap();
		for (ParkingLocation parkingLocation : lopl) {
			drawOnMap(parkingLocation, infoWindow, addToFave, addTicket);
		}
	}

	public void drawOnMap(final ParkingLocation parkingLocation,
			final MyInfoWindow infoWindow, final Button addToFave,
			final Button addTicket) {
		// create a new line, options, and path
		Polyline currentPolyLine = Polyline.create();
		PolylineOptions polyoptions = PolylineOptions.create();
		JsArray<LatLng> jsArrayPath = JsArray.createArray().cast();

		// set path
		jsArrayPath.push(LatLng.create(parkingLocation.getStartLat(),
				parkingLocation.getStartLong()));
		jsArrayPath.push(LatLng.create(parkingLocation.getEndLat(),
				parkingLocation.getEndLong()));

		currentPolyLine.setMap(theMap);
		polyoptions.setClickable(true);
		polyoptions.setStrokeColor(parkingLocation.getColor());
		// polylineoptions set color from an enumeration of color/price
		// references
		currentPolyLine.setOptions(polyoptions);
		currentPolyLine.setPath(jsArrayPath);

		Polyline.ClickHandler clickHandler = new Polyline.ClickHandler() {
			@Override
			public void handle(MouseEvent event) {
				parkingLocation.displayPopup(theMap, infoWindow, addToFave,
						addTicket);

			}
		};

		currentPolyLine.addClickListener(clickHandler);

		// Add this polyline to the public set (handles deletion)
		polylines.add(currentPolyLine);

	}

	// Clear all polylines from the map
	public void clearMap() {
		Iterator<Polyline> polyit = polylines.iterator();
		while (polyit.hasNext()) {
			Polyline polyline = (Polyline) polyit.next();
			polyline.setMap(null);
		}
	}

	public void drawCircle(LatLng searchPoint, double maxRadius) {
		circle.setCenter(searchPoint);
		circle.setRadius(maxRadius);
		circle.setMap(theMap);
		CircleOptions options = CircleOptions.create();
		options.setStrokeOpacity(.5);
		options.setStrokeWeight(1);
		options.setFillOpacity(.1);
		options.setZindex(-1);
		circle.setOptions(options);
	}

	public void clearCircle() {
		circle.setMap(null);
	}
	
	public void setMarker(LatLng latlong) {
		System.out.println("Setting marker");
		marker.setPosition(latlong);
		marker.setVisible(true);
		marker.setMap(theMap);
	}
	
	public void clearMarker() {
		System.out.println("Clearing marker");
		marker.setVisible(false);
	}

}
