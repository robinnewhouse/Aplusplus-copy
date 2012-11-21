package ca.ubc.cpsc310.parkme.client.services.parking;

import java.io.Serializable;

import org.spacetimeresearch.gwt.addthis.client.AddThisWidget;

import ca.ubc.cpsc310.parkme.client.MyInfoWindow;
import ca.ubc.cpsc310.parkme.client.TicketService;
import ca.ubc.cpsc310.parkme.client.TicketServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.GoogleMap;
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

	public void displayPopup(final GoogleMap theMap,
			final MyInfoWindow infoWindow, final Button addToFave,
			final Button addTicket) {
		FaveAsync fave = GWT.create(Fave.class);
		boolean faved;
		fave.checkFave(getParkingID(), new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(Boolean result) {
				if (result) {
					displayPopup2(theMap, infoWindow, false, addToFave,
							addTicket);
				} else {
					displayPopup2(theMap, infoWindow, true, addToFave,
							addTicket);
				}
			}
		});

	}

	public void displayPopup2(GoogleMap theMap, MyInfoWindow infoWindow,
			boolean enabled, final Button addToFave, Button addTicket) {

		// center map on midpoint of the lat/longs & zoom in
		LatLng latlong = LatLng.create((getStartLat() + getEndLat()) / 2,
				(getStartLong() + getEndLong()) / 2);
		// theMap.setCenter(latlong);
		// theMap.setZoom(17);

		// final Button addToFave;
		if (enabled) {
			// addToFave = new Button("Add to Fave");
			addToFave.setText("Add to Fave");
		} else {
			// addToFave = new Button("Already Faved");
			addToFave.setText("Already Faved");
		}

		addToFave.setEnabled(enabled);
		HTML info = new HTML("<b>" + getStreet() + "</b><br><u>Rate:</u> $"
				+ getPrice() + "/hr<br><u>Limit:</u> " + getLimit() + "hr/s");
		VerticalPanel main = new VerticalPanel();
		main.add(info);
		main.add(addToFave);
		main.add(addTicket); // robin
		AddThisWidget addThisWidget = new AddThisWidget("ra-5094b7074b51725f",
				"There is a nice parking spot at " + getStreet()
				+ " with a rate of $" + getPrice() + "/hr!", 300);
		main.add(addThisWidget);
		infoWindow.setContent(main);
		infoWindow.setPosition(latlong);
		infoWindow.open(theMap);

		addToFave.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				addToFave.setEnabled(false);
				addToFave.setText("FAVED!");
			}

		});

		addTicket.addClickHandler(new ClickHandler() {

			// robin

			@Override
			public void onClick(ClickEvent event) {

				String msg = "Please enter the amount of your paking fine";
				Boolean correct = false;
				NumberFormat formatter = NumberFormat.getCurrencyFormat("CAD");
				String stringAmount;
				Double doubleAmount = null;
				String formattedFine = null;
				while (correct == false) {
					stringAmount = Window.prompt(msg, "00.00");
					try {
						stringAmount = stringAmount.replaceAll("[^\\d.]", "");
						doubleAmount = Double.parseDouble(stringAmount);
						formattedFine = formatter.format(doubleAmount);
						correct = Window
								.confirm("Upload the following data: \n you were fined "
										+ formattedFine + " is this correct?");
					} catch (NullPointerException e) {
						e.printStackTrace();
						break;
					} catch (Exception e) {
						msg = "Please enter the amount of your paking fine. Format must be: 00.00";
						e.printStackTrace();
					}

				}
				if (correct) {

					TicketServiceAsync ticket = GWT.create(TicketService.class);
					ticket.addTicket(parkingID, doubleAmount,
							new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							Window.alert("ticket was successfully uploaded to the server. Thank you");
						}

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("There was an error uploading ticket");
						}
					});

				} else
					Window.alert("ticket not uploaded");
			}

		});

		/**
		 * addToFave.addClickHandler(new ClickHandler() {
		 * 
		 * @Override public void onClick(ClickEvent event) {
		 * 
		 *           FaveAsync fave = GWT.create(Fave.class);
		 *           fave.addFave(getParkingID(), new AsyncCallback<Void>() {
		 * @Override public void onSuccess(Void result) { }
		 * @Override public void onFailure(Throwable caught) { // refresh
		 *           favorites
		 * 
		 *           } }); addToFave.setEnabled(false);
		 *           addToFave.setText("FAVED!"); }
		 * 
		 *           });
		 **/
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
			double startLat, double startLong, double endLat, double endLong,
			String street, String color) {
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

	public void displayPopup(GoogleMap theMap, MyInfoWindow infoWindow) {
		LatLng latlong = LatLng.create((getStartLat() + getEndLat()) / 2,
				(getStartLong() + getEndLong()) / 2);
		HTML info = new HTML("<b>" + getParkingID() + "<br>" + getStreet() + "</b><br><u>Rate:</u> $"
				+ getPrice() + "/hr<br><u>Limit:</u> " + getLimit() + "hr/s");
		infoWindow.setContent(info);
		infoWindow.setPosition(latlong);
		infoWindow.open(theMap);


	}

	/**
	 * private void checkIfAlreadyFave() { FaveAsync fave =
	 * GWT.create(Fave.class); boolean faved; fave.checkFave(getParkingID(), new
	 * AsyncCallback<Boolean>() {
	 * 
	 * @Override public void onFailure(Throwable caught) {
	 * 
	 *           }
	 * @Override public void onSuccess(Boolean result) { if (result) {
	 *           displayFavePopup(); } else {
	 * 
	 *           } } }); }
	 **/
}
