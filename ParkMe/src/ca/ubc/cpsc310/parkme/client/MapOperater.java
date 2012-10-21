package ca.ubc.cpsc310.parkme.client;

import java.util.ArrayList;

import com.google.gwt.core.client.JsArray;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.Polyline;
import com.google.maps.gwt.client.PolylineOptions;

public class MapOperater {

	private GoogleMap theMap;
	LatLng location1 = LatLng.create(49.251, -123.119);
	LatLng location2 = LatLng.create(49.281, -123.119);
	LatLng location3 = LatLng.create(49.261, -123.129);
	LatLng location4 = LatLng.create(49.271, -123.139);

	public MapOperater(GoogleMap map) {
		this.theMap = map;
		// testStuff();
	}

	public void testStuff() {

		GoogleMap aNewMap;
		// aNewMap.setM

		theMap.setCenter(location1);// issue setting center. may be a problem

		// theMap.setZoom(2); same issue. seems I can't do anything with map
		// except reference it in other methods

		LatLng[] latlongs = new LatLng[2];
		latlongs[0] = location1;
		latlongs[1] = location2;
		Polyline polyline1 = Polyline.create();
		polyline1.setMap(theMap);
		JsArray<LatLng> jsarraypath = JsArray.createArray().cast();
		// how to make a polypath
		jsarraypath.push(location1);
		jsarraypath.push(location2);

		// x: 49.251 , y: -123.119;
		PolylineOptions polyoptions = PolylineOptions.create();
		polyoptions.setClickable(true);
		polyline1.setOptions(polyoptions);
		polyline1.setPath(jsarraypath);

		Marker marker1 = Marker.create();
		Marker marker2 = Marker.create();
		marker1.setMap(theMap);
		marker2.setMap(theMap);
		marker1.setPosition(location1);
		marker2.setPosition(location2);
	}

	public void testStuff(ArrayList<ParkingLocation> lopl) {
		for (ParkingLocation parkingLocation : lopl) {
			drawOnMap(parkingLocation);
		}
	}
	
	public void drawLocs(ParkingLocation[] lopl) {
		for (ParkingLocation parkingLocation : lopl) {
			drawOnMap(parkingLocation);
		}
	}

	private void drawOnMap(ParkingLocation parkingLocation) {
		Polyline currentPolyLine = Polyline.create();
		PolylineOptions polyoptions = PolylineOptions.create();
		JsArray<LatLng> jsArrayPath = JsArray.createArray().cast();

		jsArrayPath.push(LatLng.create(parkingLocation.getStartLat(),
				parkingLocation.getStartLong()));
		jsArrayPath.push(LatLng.create(parkingLocation.getEndLat(),
				parkingLocation.getEndLong()));

		currentPolyLine.setMap(theMap);
		polyoptions.setClickable(true);
		// polylineoptions set color from an enumeration of color/price
		// references
		currentPolyLine.setOptions(polyoptions);
		currentPolyLine.setPath(jsArrayPath);

	}
}
