package ca.ubc.cpsc310.parkme.client;

import java.util.ArrayList;


import ca.ubc.cpsc310.parkme.client.ParkingLocation;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.GeocoderAddressComponent;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.InfoWindow;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ParkMe implements EntryPoint {

	// GEOCODER
	private Geocoder geocoder = Geocoder.create();
	private InfoWindow infoWindow = InfoWindow.create();
	
	// FILTER UI STUFF

	private Button getAddressesButton = new Button("Load Street Information");

	private TextBox priceFilterTextBox = new TextBox();
	private TextBox timeFilterTextBox = new TextBox();

	private Label maxPriceLabel = new Label("Maximum Price: ");

	private HorizontalPanel pricePanel = new HorizontalPanel();
	private HorizontalPanel timePanel = new HorizontalPanel();

	private Label minTimeLabel = new Label("Minimum Time Limit: ");

	private Button loadDataButton = new Button("Load Data");
	private Button displayDataButton = new Button("Display All Data");
	private Button clearDataButton = new Button("Clear Data");
	private VerticalPanel mainPanel = new VerticalPanel();
	private Button filterButton = new Button("Filter Results");

	private ScrollPanel resultsScroll = new ScrollPanel();
	private MapOperater mapOperator;
	private HorizontalPanel tabPanel = new HorizontalPanel();
	private GoogleMap theMap;
	private HorizontalPanel mainHorzPanel = new HorizontalPanel();
	private VerticalPanel leftVertPanel = new VerticalPanel();
	private Button favoritesButton = new Button("Favorites");
	private Button historyButton = new Button("History");
	private FlexTable resultsFlexTable = new FlexTable();
	private FlexTable idTable = new FlexTable();
	private VerticalPanel rightVertPanel = new VerticalPanel();
	private HorizontalPanel TitleHorzPanel = new HorizontalPanel();
	private Label titleLabel = new Label("Park Me");
	private Button loginButton = new Button("Login");
	private VerticalPanel SearchPanel = new VerticalPanel(); // TODO - Figure
	// out how to
	// implement
	// this
	// properly!
	private VerticalPanel mapPanel = new VerticalPanel(); // TODO - Frances
	// implement this
	// properly - just
	// reserving space
	// now!
	private final LoadDataServiceAsync loadDataService = GWT
			.create(LoadDataService.class);
	private final FilterServiceAsync filterService = GWT
			.create(FilterService.class);

	private final ParkingLocServiceAsync parkService = GWT
			.create(ParkingLocService.class);


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		RootPanel.get("parkMe").add(mainPanel);

		pricePanel.add(maxPriceLabel);
		pricePanel.add(priceFilterTextBox);

		priceFilterTextBox.setHeight("1em");
		timeFilterTextBox.setHeight("1em");

		timePanel.add(minTimeLabel);
		timePanel.add(timeFilterTextBox);

		mainPanel.add(pricePanel);
		mainPanel.add(timePanel);

		tabPanel.add(historyButton);
		tabPanel.add(favoritesButton);
		tabPanel.add(loadDataButton);
		tabPanel.add(displayDataButton);
		tabPanel.add(clearDataButton);
		tabPanel.add(filterButton);
		tabPanel.add(getAddressesButton);
		mainPanel.add(tabPanel);
		resultsFlexTable.setCellPadding(5);

		resultsScroll.add(resultsFlexTable);
		mainHorzPanel.add(resultsScroll);
		mainHorzPanel.add(rightVertPanel);

		mainPanel.add(mainHorzPanel);

		// Set sizes for elements
		resultsScroll.setSize(0.3 * Window.getClientWidth() - 20 + "px", "100%");
		resultsFlexTable.setSize(0.3 * Window.getClientWidth() - 20 + "px", "100%");
		mainHorzPanel.setSize("100%", Window.getClientHeight() - 160 + "px");
		rightVertPanel.setSize(0.7 * Window.getClientWidth() - 20 + "px", "100%");
		mapPanel.setSize("100%", "100%");
		mainPanel.setSpacing(10);
		mapPanel.setBorderWidth(1);

		// Set up map options
		MapOptions options = MapOptions.create();
		options.setCenter(LatLng.create(49.251, -123.119));
		options.setZoom(11);
		options.setMapTypeId(MapTypeId.ROADMAP);
		options.setDraggable(true);
		options.setMapTypeControl(true);
		options.setScaleControl(true);
		options.setScrollwheel(true);

		// Add map to mapPanel
		theMap = GoogleMap.create(mapPanel.getElement(), options);
		rightVertPanel.add(mapPanel);

		// testing - Robin//
		mapOperator = new MapOperater(theMap);
		// mapOperator.testStuff();

		ParkingLocation testLocation1 = new ParkingLocation("test1", 1.00,
				2.00, 49.251, -123.119, 49.261, -123.129, "street1");
		ParkingLocation testLocation2 = new ParkingLocation("test2", 2.00,
				2.00, 49.271, -123.139, 49.281, -123.149, "street2");
		ParkingLocation testLocation3 = new ParkingLocation("test3", 3.00,
				2.00, 49.291, -123.159, 49.301, -123.169, "street3");

		ArrayList<ParkingLocation> testList = new ArrayList<ParkingLocation>();
		testList.add(testLocation1);
		testList.add(testLocation2);
		testList.add(testLocation3);

		mapOperator.testStuff(testList);

		// Listen for mouse events on the Load Data button.
		// In the end, this should only be accessible by an admin
		loadDataButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				loadData();
			}
		});

		// Listen for mouse events on the Display Data button.
		displayDataButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				displayData();
			}
		});

		// Listen for mouse events on the Clear Data button.
		clearDataButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				resultsFlexTable.removeAllRows();
			}
		});

		filterButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				displayFilter();
			}
		});

		getAddressesButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getAllLocations();
			}
		});

		resultsFlexTable.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int row = resultsFlexTable.getCellForEvent(event).getRowIndex();
				String parkingID = idTable.getText(row, 0);
				System.out.println("I have clicked on parking with ID: "
						+ parkingID);
				// get corresponding ParkingLocation with parkingID
				parkService.getParking(parkingID,
						new AsyncCallback<ParkingLocation>() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(ParkingLocation result) {
						// TODO Auto-generated method stub
						displayPopup(result);
					}

				});

			}
		});

	}

	private void loadData() {
		loadDataService.loadData(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("ERROR LOADING DATA");
			}

			@Override
			public void onSuccess(Void result) {
				Window.alert("DATA LOADED SUCCESSFULLY. \n Click on Load Street Information next.");
			}

		});
	}

	private void displayData() {
		loadDataService.getParking(new AsyncCallback<ParkingLocation[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error getting parking");
			}

			@Override
			public void onSuccess(ParkingLocation[] result) {

				displayParkings(result);
				Window.alert("Successfully displayed data");
			}

		});
	}

	private void displayParkings(ParkingLocation[] parkingLocs) {
		for (ParkingLocation p : parkingLocs) {
			displayParking(p);
		}
	}

	private void displayParking(final ParkingLocation parkingLoc) {

		VerticalPanel info = new VerticalPanel();
		HTML street = new HTML("<b>" + parkingLoc.getStreet() + "</b>");
		HTML rate = new HTML("<u>Rate:</u> $" + parkingLoc.getPrice() + "/hr");
		HTML limit = new HTML("<u>Limit:</u> " + parkingLoc.getLimit() + "hr/s");
		info.add(street);
		info.add(rate);
		info.add(limit);
		int row = resultsFlexTable.getRowCount();
		if (parkingLoc.getPrice() < 2) {
			resultsFlexTable.getRowFormatter().addStyleName(row, "parking1");
		} else if (parkingLoc.getPrice() < 3 && parkingLoc.getPrice() >= 2) {
			resultsFlexTable.getRowFormatter().addStyleName(row, "parking2");
		} else if (parkingLoc.getPrice() >= 3 && parkingLoc.getPrice() < 4) {
			resultsFlexTable.getRowFormatter().addStyleName(row, "parking3");
		} else if (parkingLoc.getPrice() >= 4) {
			resultsFlexTable.getRowFormatter().addStyleName(row, "parking4");
		}
		resultsFlexTable.setWidget(row, 0, info);

		// we will store the parkingIDs in a different table but will never be
		// displayed
		// this is for retrieving information about each row
		idTable.setText(row, 0, parkingLoc.getParkingID());
	}

	private void displayFilter() {

		double maxPrice = Double.parseDouble(priceFilterTextBox.getText());
		double minTime = Double.parseDouble(timeFilterTextBox.getText());

		Criteria crit = new Criteria(0, maxPrice, minTime);
		filterService.getParking(crit, new AsyncCallback<ParkingLocation[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error getting parking");
			}

			@Override
			public void onSuccess(ParkingLocation[] result) {
				Window.alert("Successfully displayed filtered data");
				mapOperator.drawLocs(result);
				displayParkings(result);
				
			}

		});
	}


	private void getLocations(final ParkingLocation[] parkingLocs) {

		final int size = parkingLocs.length;
		Window.alert("Fetching street information for " + size
				+ " parking locations.");

		Timer refreshTimer = new Timer() {
			int i = 0;

			@Override
			public void run() {
				if (i < size) {
					getLocation(parkingLocs[i]);
					i++;
				} else {
					this.cancel();
				}
			}
		};
		refreshTimer.scheduleRepeating(2000);

	}

	private void getLocation(final ParkingLocation parkingLoc) {

		if (parkingLoc.getStreet().equals("Vancouver")) {
			LatLng latlong = LatLng.create(parkingLoc.getStartLat(),
					parkingLoc.getStartLong());
			GeocoderRequest request = GeocoderRequest.create();
			request.setLocation(latlong);
			geocoder.geocode(request, new Geocoder.Callback() {
				@Override
				public void handle(JsArray<GeocoderResult> results,
						GeocoderStatus status) {
					// TODO Auto-generated method stub
					if (status == GeocoderStatus.OK) {
						GeocoderResult location = results.get(0);
						GeocoderAddressComponent addressComp = location
								.getAddressComponents().get(1);
						String street = addressComp.getLongName();
						parkingLoc.setStreet(street);
						loadDataService.setStreet(street,
								parkingLoc.getParkingID(),
								new AsyncCallback<Void>() {

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
							}

							@Override
							public void onSuccess(Void result) {
								// TODO Auto-generated method stub
							}
						});
					} else {
						Window.alert("ERROR " + status.getValue()
								+ "\n please wait");
						getLocation(parkingLoc);
					}
				}

			});
			return;

		} else {

			return;

		}

	}

	private void getAllLocations() {
		loadDataService
		.getUnknownStreets(new AsyncCallback<ParkingLocation[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error getting parking");
			}

			@Override
			public void onSuccess(ParkingLocation[] result) {
				Window.alert("Loading Parking Streets. Please Wait.");
				getLocations(result);
			}

		});
	}

	private void displayPopup(ParkingLocation parkingLoc) {
	
		// center map on midpoint of the lat/longs & zoom in
		LatLng latlong = LatLng.create(
				(parkingLoc.getStartLat() + parkingLoc.getEndLat()) / 2,
				(parkingLoc.getStartLong() + parkingLoc.getEndLong()) / 2);
		theMap.setCenter(latlong);
		theMap.setZoom(17);
		
		// display a pop-up with corresponding information
		infoWindow.setContent("<b>" + parkingLoc.getStreet() + "</b><br><u>Rate:</u> $" 
		+ parkingLoc.getPrice() + "/hr<br><u>Limit:</u> " + parkingLoc.getLimit() + "hr/s");
		infoWindow.setPosition(latlong);
		infoWindow.open(theMap);

	}
}