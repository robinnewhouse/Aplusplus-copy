package ca.ubc.cpsc310.parkme.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
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

	// LOGIN
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label("Please sign in to your Google Account to access the ParkMe application.");

	// FAVORITES
	private Button addToFave = new Button("Add to Favorites");
	private Button getFavesButton = new Button("My Favorites");

	// GEOCODER
	private Geocoder geocoder = Geocoder.create();
	private InfoWindow infoWindow = InfoWindow.create();
	private NXInfoWindow infoWindow2 = NXInfoWindow.create(0L);
	private boolean zoom = false;

	// FILTER UI STUFF
	private Button setColor = new Button("Set Colors");
	private Button getAddressesButton = new Button("Load Street Information");
	private LoginInfo loginInfo = null;

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
	private Button historyButton = new Button("History");
	private FlexTable resultsFlexTable = new FlexTable();
	private FlexTable idTable = new FlexTable();
	private VerticalPanel rightVertPanel = new VerticalPanel();
	private Label titleLabel = new Label("Park Me");
	private Button loginButton = new Button("Login");

	private HorizontalPanel searchPanel = new HorizontalPanel();
	private TextBox searchBox = new TextBox();
	private Label searchLabel = new Label("Enter Address: ");
	private Button searchButton = new Button("Search");

	private List<ParkingLocation> allParkings = new ArrayList<ParkingLocation>();
	private int totalNum = 0;

	private VerticalPanel mapPanel = new VerticalPanel();


	private final LoadDataServiceAsync loadDataService = GWT.create(LoadDataService.class);
	private final FilterServiceAsync filterService = GWT.create(FilterService.class);
	private final ParkingLocServiceAsync parkService = GWT.create(ParkingLocService.class);
	private final FaveAsync fave = GWT.create(Fave.class);


	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				if(loginInfo.isLoggedIn()) {
					loadParkMe(); } 
				else {
					loadLogin();
				}
			}
		});



	}

	private void loadParkMe() {
		initializeLayout();
		createMap();
		addListenersToButtons();
		addListenerToResults();
		downloadData();
	}

	private void addListenerToResults() {
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
						if (zoom == false) {
							zoom = true;
							theMap.setZoom(17);
						}
						result.displayPopup(theMap, infoWindow2);

						// displayPopup(result);
					}

				});

			}
		});
	}

	private void addListenersToButtons() {
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
				mapOperator.clearMap();
				resultsFlexTable.removeAllRows();
			}
		});

		// Listen for mouse events on the filter Data button.
		filterButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				displayFilter();
			}
		});

		// Listen for mouse events on the get address button.
		getAddressesButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				getAllLocations();
			}
		});

		// Listen for mouse events on the search button.
		searchButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String address = searchBox.getText();
				searchLoc(address);
			}
		});
		
		getFavesButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				fave.getFaves(new AsyncCallback<String[]>() {

					@Override
					public void onFailure(Throwable caught) {}

					@Override
					public void onSuccess(String[] result) {
						resultsFlexTable.removeAllRows();
						if (result.length == 0) {
							resultsFlexTable.setText(0, 0, "You haven't added anything to favorites yet.");

						}
								
						else {
						parkService.getParkings(result, new AsyncCallback<ParkingLocation[]>() {

							@Override
							public void onFailure(Throwable caught) {}

							@Override
							public void onSuccess(ParkingLocation[] result) {
								displayParkings(result);
							}
						});}
					}
				});
			}
		});
	}

	private void createMap() {
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
	}

	private void initializeLayout() {
		signOutLink.setHref(loginInfo.getLogoutUrl());
		RootPanel.get("parkMe").add(mainPanel);

		pricePanel.add(maxPriceLabel);
		pricePanel.add(priceFilterTextBox);

		priceFilterTextBox.setHeight("1em");
		timeFilterTextBox.setHeight("1em");

		timePanel.add(minTimeLabel);
		timePanel.add(timeFilterTextBox);

		searchBox.setHeight("1em");
		searchPanel.add(searchLabel);
		searchPanel.add(searchBox);
		searchPanel.add(searchButton);

		mainPanel.add(searchPanel);
		mainPanel.add(pricePanel);
		mainPanel.add(timePanel);

		// ADMIN CONTROLS:
		//	tabPanel.add(loadDataButton);
		//	tabPanel.add(getAddressesButton);
		//	tabPanel.add(setColor);
		tabPanel.add(historyButton);
		tabPanel.add(getFavesButton);
		tabPanel.add(displayDataButton);
		tabPanel.add(clearDataButton);
		tabPanel.add(filterButton);
		tabPanel.add(signOutLink);

		mainPanel.add(tabPanel);
		resultsFlexTable.setCellPadding(5);

		resultsScroll.add(resultsFlexTable);
		mainHorzPanel.add(resultsScroll);
		mainHorzPanel.add(rightVertPanel);

		mainPanel.add(mainHorzPanel);

		// Set sizes for elements
		resultsScroll
		.setSize(0.3 * Window.getClientWidth() - 20 + "px", "100%");
		resultsFlexTable.setSize(0.3 * Window.getClientWidth() - 20 + "px",
				"100%");
		mainHorzPanel.setSize("100%", Window.getClientHeight() - 190 + "px");
		rightVertPanel.setSize(0.7 * Window.getClientWidth() - 20 + "px",
				"100%");
		mapPanel.setSize("100%", "100%");
		mainPanel.setSpacing(10);
		mapPanel.setBorderWidth(1);
	}

	private void loadData() {
		loadDataService.loadData(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("ERROR LOADING DATA");
			}

			@Override
			public void onSuccess(Void result) {
				Window.alert("DATA LOADED SUCCESSFULLY. \nClick on Load Street Information next.");
			}

		});
	}

	private void displayData() {

		/** 
		 * 
		 * Display the data that is downloaded on the client
		 * 
		 **/
		resultsFlexTable.removeAllRows();
		ParkingLocation[] parkingLoc = allParkings.toArray(new ParkingLocation[totalNum]);
		mapOperator.clearMap();
		//mapOperator.drawLocs(parkingLoc, infoWindow);
		displayParkings(parkingLoc);


		/**
		loadDataService.getParking(new AsyncCallback<ParkingLocation[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error getting parking");
			}

			@Override
			public void onSuccess(ParkingLocation[] result) {
				resultsFlexTable.removeAllRows();
				mapOperator.drawLocs(result, infoWindow);
				displayParkings(result);
				// Window.alert("Successfully displayed data");
			}

		});**/


	}

	/**
	private void displayParkings(List<ParkingLocation> parking) {
		int size = parking.size();
		for (int i = 0; i < size; i++) {
			displayParking(parking.get(i));
		}

	}
	 **/

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
		mapOperator.drawOnMap(parkingLoc, infoWindow2);
		// we will store the parkingIDs in a different table but will never be
		// displayed
		// this is for retrieving information about each row
		idTable.setText(row, 0, parkingLoc.getParkingID());
		System.out.println("Currently printing parking " + parkingLoc.getParkingID());
	}

	private void displayFilter() {

		if (priceFilterTextBox.getText().equals("") || timeFilterTextBox.getText().equals("")) {
			resultsFlexTable.removeAllRows();
			resultsFlexTable.setText(0, 0, "Please enter values above.");
		}

		double maxPrice = Double.parseDouble(priceFilterTextBox.getText());
		double minTime = Double.parseDouble(timeFilterTextBox.getText());

		List<ParkingLocation> filtered = new ArrayList<ParkingLocation>();
		for (int i = 0; i < totalNum; i++) {
			ParkingLocation p = allParkings.get(i);
			if ((p.getPrice() <= maxPrice) && (p.getLimit() >= minTime)) {
				filtered.add(p);
			}
		}

		int length = filtered.size();
		resultsFlexTable.removeAllRows();
		mapOperator.clearMap();
		if (length == 0) {
			resultsFlexTable.setText(0, 0, "No results found.");
		}
		else {
			ParkingLocation[] parkingLoc = filtered.toArray(new ParkingLocation[length]);
			resultsFlexTable.setText(0, 0, length + " results found.");
			displayParkings(parkingLoc);
		}

		/**
		Criteria crit = new Criteria(0, maxPrice, minTime);
		filterService.getParking(crit, new AsyncCallback<ParkingLocation[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error getting parking");
			}

			@Override
			public void onSuccess(ParkingLocation[] result) {
				// Window.alert("Successfully displayed filtered data");
				int length = result.length;
				resultsFlexTable.removeAllRows();
				if (length == 0) {
					resultsFlexTable.setText(0, 0, "No results found.");
				} else {
					//mapOperator.drawLocs(result, infoWindow);

					resultsFlexTable.setText(0, 0, "Found " + length
							+ " results.");
					displayParkings(result);
				}

			}

		});**/
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

			LatLng latlong = LatLng.create(
					(parkingLoc.getStartLat() + parkingLoc.getEndLat()) / 2,
					(parkingLoc.getStartLong() + parkingLoc.getEndLong()) / 2);
			// LatLng latlong = LatLng.create(parkingLoc.getStartLat(),
			// parkingLoc.getStartLong());
			GeocoderRequest request = GeocoderRequest.create();
			request.setLocation(latlong);
			geocoder.geocode(request, new Geocoder.Callback() {
				@Override
				public void handle(JsArray<GeocoderResult> results,
						GeocoderStatus status) {
					// TODO Auto-generated method stub
					if (status == GeocoderStatus.OK) {
						GeocoderResult location = results.get(0);

						JsArray<GeocoderAddressComponent> addr = location
								.getAddressComponents();

						for (int i = 0; i < addr.length(); i++) {
							String type = addr.get(i).getTypes().toString();
							System.out.println(type);
							if (type.equals("route")) {
								String street = addr.get(i).getLongName();
								System.out.println(street);
								parkingLoc.setStreet(street);

								loadDataService.setStreet(street,
										parkingLoc.getParkingID(),
										new AsyncCallback<Void>() {

									@Override
									public void onFailure(
											Throwable caught) {
										// TODO Auto-generated method
										// stub
									}

									@Override
									public void onSuccess(Void result) {
										// TODO Auto-generated method
										// stub
									}
								});
								return;
							}
						}

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

	/**
	 * private void displayPopup(ParkingLocation parkingLoc) {
	 * 
	 * // center map on midpoint of the lat/longs & zoom in LatLng latlong =
	 * LatLng.create( (parkingLoc.getStartLat() + parkingLoc.getEndLat()) / 2,
	 * (parkingLoc.getStartLong() + parkingLoc.getEndLong()) / 2);
	 * theMap.setCenter(latlong); theMap.setZoom(17);
	 * 
	 * // display a pop-up with corresponding information
	 * infoWindow.setContent("<b>" + parkingLoc.getStreet() + "</b><br>
	 * <u>Rate:</u> $" + parkingLoc.getPrice() + "/hr<br>
	 * <u>Limit:</u> " + parkingLoc.getLimit() + "hr/s");
	 * infoWindow.setPosition(latlong); infoWindow.open(theMap);
	 * 
	 * }
	 **/
	private void searchLoc(final String address) {
		GeocoderRequest request = GeocoderRequest.create();
		request.setAddress(address + " Vancouver");
		request.setRegion("ca");
		geocoder.geocode(request, new Geocoder.Callback() {

			@Override
			public void handle(JsArray<GeocoderResult> results,
					GeocoderStatus status) {
				if (status == GeocoderStatus.OK) {
					LatLng latlong = results.get(0).getGeometry().getLocation();
					String addr = results.get(0).getFormattedAddress();
					theMap.setCenter(latlong);
					infoWindow.setContent(addr);
					infoWindow.setPosition(latlong);
					infoWindow.open(theMap);
				}

			}

		});

	}

	private void downloadData() {
		Window.alert("Please wait while data is loading");
		loadDataService.getParking(new AsyncCallback<ParkingLocation[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error getting parking");
			}

			@Override
			public void onSuccess(ParkingLocation[] result) {

				for (ParkingLocation p : result) {
					allParkings.add(p);
				}
				totalNum = allParkings.size();
				Window.alert("Data has been downloaded to client successfully.");
			}

		});
	}
	
	private void loadLogin() {
		// Assemble login panel.
		signInLink.setHref(loginInfo.getLoginUrl());
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootPanel.get("parkMe").add(loginPanel);
	}
	
	private void handleError(Throwable error) {
		Window.alert(error.getMessage());
		if (error instanceof NotLoggedInException) {
			Window.Location.replace(loginInfo.getLogoutUrl());
		}
	}
}