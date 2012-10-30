package ca.ubc.cpsc310.parkme.client;

import java.io.Serializable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.InfoWindow;
import com.google.maps.gwt.client.LatLng;


public class ParkingLocation implements Serializable {
	private String parkingID;
	private double price;
	private double limit;
	private double startLat;
	private double startLong;
	private double endLat;
	private double endLong;
	private String street;
	private String color;

	public void displayPopup(GoogleMap theMap, NXInfoWindow infoWindow) {
		// center map on midpoint of the lat/longs & zoom in
		LatLng latlong = LatLng.create(
				(getStartLat() + getEndLat()) / 2,
				(getStartLong() + getEndLong()) / 2);
		//theMap.setCenter(latlong);
		//theMap.setZoom(17);

		// display a pop-up with corresponding information
		//InfoWindow infoWindow = InfoWindow.create();
/**
		infoWindow.setContent("<b>" + getStreet() + "</b><br><u>Rate:</u> $" 
				+ getPrice() + "/hr<br><u>Limit:</u> " + getLimit() + "hr/s");
		infoWindow.setPosition(latlong);
		infoWindow.open(theMap);
**/
		Button addToFave = new Button("Add to Fave");
		HTML info = new HTML("<b>" + getStreet() + "</b><br><u>Rate:</u> $" 
				+ getPrice() + "/hr<br><u>Limit:</u> " + getLimit() + "hr/s");
		VerticalPanel main = new VerticalPanel();
		main.add(info);
		main.add(addToFave);

		infoWindow.setContent(main);
		infoWindow.setPosition(latlong);
		infoWindow.open(theMap);

		//infoWindow.setContent("<b>" + getStreet() + "</b><br><u>Rate:</u> $" 
		//		+ getPrice() + "/hr<br><u>Limit:</u> " + getLimit() + "hr/s");
		//infoWindow.setPosition(latlong);

		//infoWindow.open(theMap);

		addToFave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				FaveAsync fave = GWT.create(Fave.class);
				fave.addFave(getParkingID(), new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
			}

		});
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public ParkingLocation() {
	}

	public ParkingLocation(String parkingID, double price, double limit,
			double startLat, double startLong, double endLat, double endLong, String street, String color) {
		this.parkingID = parkingID;
		this.price = price;
		this.limit = limit;
		this.startLat = startLat;
		this.startLong = startLong;
		this.endLat = endLat;
		this.endLong = endLong;
		this.street = street;
		this.color = color;
	}

	public String getParkingID() {
		return parkingID;
	}

	public void setParkingID(String parkingID) {
		this.parkingID = parkingID;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getLimit() {
		return limit;
	}

	public void setLimit(double limit) {
		this.limit = limit;
	}

	public double getStartLat() {
		return startLat;
	}

	public void setStartLat(double startLat) {
		this.startLat = startLat;
	}

	public double getStartLong() {
		return startLong;
	}

	public void setStartLong(double startLong) {
		this.startLong = startLong;
	}

	public double getEndLat() {
		return endLat;
	}

	public void setEndLat(double endLat) {
		this.endLat = endLat;
	}

	public double getEndLong() {
		return endLong;
	}

	public void setEndLong(double endLong) {
		this.endLong = endLong;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}
}

