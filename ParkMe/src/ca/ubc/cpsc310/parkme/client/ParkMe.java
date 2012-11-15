package ca.ubc.cpsc310.parkme.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.List;


import com.google.gwt.core.client.JavaScriptObject;

import ca.ubc.cpsc310.parkme.client.sdk.FBCore;
import ca.ubc.cpsc310.parkme.client.sdk.FBEvent;
import ca.ubc.cpsc310.parkme.client.sdk.FBXfbml;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.DirectionsRenderer;
import com.google.maps.gwt.client.DirectionsRequest;
import com.google.maps.gwt.client.DirectionsResult;
import com.google.maps.gwt.client.DirectionsService;
import com.google.maps.gwt.client.DirectionsStatus;
import com.google.maps.gwt.client.Geocoder;
import com.google.maps.gwt.client.GeocoderAddressComponent;
import com.google.maps.gwt.client.GeocoderRequest;
import com.google.maps.gwt.client.GeocoderResult;
import com.google.maps.gwt.client.GeocoderStatus;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;
import com.google.maps.gwt.client.TravelMode;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ParkMe implements EntryPoint, ValueChangeHandler<String> {

	// FACEBOOK EVENT STUFF
	// private static final String apiKey = "464072253644385";
	//FOR LOCAL:
	 private static final String apiKey = "219605264787363";
	private FBCore fbCore = GWT.create(FBCore.class);
	private FBEvent fbEvent = GWT.create(FBEvent.class);
	private VerticalPanel fbPanel = new VerticalPanel ();

	private boolean status = true;
	private boolean xfbml = true;
	private boolean cookie = true;


	// TABPANEL
	private TabPanel tabs = new TabPanel();
	private FlowPanel flowpanel;

	// LOGIN
	private Anchor signInLink = new Anchor("Sign In");
	private Anchor signOutLink = new Anchor("Sign Out");
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label loginLabel = new Label("Please sign in to your Google Account to access the ParkMe application.");
	private UserInfoClient userInfo = new UserInfoClient();


	// SET USER TYPE
	private VerticalPanel setUserPanel = new VerticalPanel();
	private Label setUserLabel = new Label("Please select what type of user you are:");
	private RadioButton driverButton = new RadioButton("userTypes", "Driver");
	private RadioButton busOwnButton = new RadioButton("userTypes", "Business Owner");
	private RadioButton adminButton = new RadioButton("userTypes", "Administrator");
	private Button setUserButton = new Button("Continue");

	// FAVORITES, RESULTS & HISTORY
	private List<String> faveList = new ArrayList<String>();
	private List<String> idList = new ArrayList<String>();
	private List<String> histList = new ArrayList<String>();
	private ScrollPanel resultsScroll = new ScrollPanel();
	private ScrollPanel faveScroll = new ScrollPanel();
	private ScrollPanel histScroll = new ScrollPanel();
	private FlexTable resultsFlexTable = new FlexTable();
	private FlexTable faveFlexTable = new FlexTable();
	private FlexTable histFlexTable = new FlexTable();

	// STATISTICS
	private ScrollPanel statsScroll = new ScrollPanel();

	// average price
	private VerticalPanel avgPriceVP = new VerticalPanel();
	private Label avgPriceLabel = new Label("Enter an address to calculate the average price around that location:");
	private TextBox avgPriceAddress = new TextBox();
	private Label avgPriceRadiusLbl = new Label("Radius: ");
	private TextBox avgPriceRadius = new TextBox();
	private Button avgPriceButton = new Button("Calculate Average");
	private Label avgPrice = new Label("");

	// GEOCODER
	private Geocoder geocoder = Geocoder.create();
	private MyInfoWindow infoWindow = MyInfoWindow.create(0L);
	private boolean zoom = false;

	// SORTING
	private Label sortLabel = new Label("Sort by:");
	private ListBox sortBox = new ListBox();
	private HorizontalPanel sortPanel = new HorizontalPanel();

	// FILTER UI STUFF
	private Button setColor = new Button("Set Colors");
	private Button getAddressesButton = new Button("Load Street Information");
	private LoginInfo loginInfo = null;

	private Slider priceFilterSlider = new Slider(10);
	private Slider timeFilterSlider = new Slider(3);
	private Slider radiusFilterSlider = new Slider(1000);

	private Label maxPriceLabel = new Label("Maximum Price: ");
	private Label maxRadiusLabel = new Label("Walking Distance:");
	private Label minTimeLabel = new Label("Minimum Time Limit: ");

	private Label maxPriceValueLabel = new Label("$5.00 / hr");
	private Label minTimeValueLabel = new Label("");
	private Label maxRadiusValueLabel = new Label("1000 m");

	private AbsolutePanel filterPanel = new AbsolutePanel();

	private Button loadDataButton = new Button("Load Data");
	private Button displayDataButton = new Button("Display All Data");
	private Button clearDataButton = new Button("Clear Data");
	private HorizontalPanel mainPanel = new HorizontalPanel();
	private Button filterButton = new Button("Filter Results");
	private Button downloadData = new Button("Download Data to Client");

	// MAP
	private MapOperater mapOperator;
	private GoogleMap theMap;
	private double defaultZoom = 10;

	// MAIN PANELS
	private VerticalPanel leftVertPanel = new VerticalPanel();
	private AbsolutePanel mapPanel = new AbsolutePanel();
	private VerticalPanel rightVertPanel = new VerticalPanel();  // delete this
	private Label titleLabel = new Label("Park Me");

	// SEARCHING
	private HorizontalPanel searchPanel = new HorizontalPanel();
	private TextBox searchBox = new TextBox();
	private Label searchLabel = new Label("Enter Address: ");
	private Button searchButton = new Button("Search");

	private List<ParkingLocation> allParkings = new ArrayList<ParkingLocation>();
	private int totalNum = 0;

	// The most recent location searched for
	private JsArray<GeocoderResult> searchResult;

	private final LoadDataServiceAsync loadDataService = GWT.create(LoadDataService.class);
	private final FilterServiceAsync filterService = GWT.create(FilterService.class);
	private final ParkingLocServiceAsync parkService = GWT.create(ParkingLocService.class);
	private final FaveAsync fave = GWT.create(Fave.class);
	private final UserInfoServiceAsync userInfoService = GWT.create(UserInfoService.class);

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
				if(!loginInfo.isLoggedIn()) {
					loadLogin();
				} else if (true) {
					loadFacebook();
					//loadSetUserType();
				} else {
					loadParkMe();
				}
			}
		});
	}

	private void loadParkMe() {
		loadUserInfo();
		initializeFlexTables();
		initializeLayout();
		createMap();
		addListenersToButtons();
		addListenerToResults();

		//	addListenerToTabs();
		//  initializeSliderValues();
		//downloadData();
		//displayData();


		addListenersToSliders();

	}

	private void loadFacebook() {
		fbCore.init(apiKey, status, cookie, xfbml);
		System.out.println("load facebook");
		
		fbPanel.add ( new HTML ( "This app uses Facebook Connect. Please click to login " ) );
		fbPanel.add ( new HTML ( "<fb:login-button autologoutlink='true' scope='publish_stream,read_stream,create_event' /> " ) );
		
		class SessionChangeCallback implements AsyncCallback<JavaScriptObject> {
			public void onSuccess ( JavaScriptObject response ) {
				System.out.println("SessionChangeCallback");
				renderFB();
			}

			@Override
			public void onFailure(Throwable caught) {
				throw new RuntimeException ( caught );
			}
		}

		SessionChangeCallback sessionChangeCallback = new SessionChangeCallback ();
		fbEvent.subscribe("auth.sessionChange", sessionChangeCallback);
		fbEvent.subscribe("auth.ResponseChange", sessionChangeCallback);
		fbEvent.subscribe("auth.login", sessionChangeCallback);
		fbEvent.subscribe("auth.logout", sessionChangeCallback);
		//renderFB();

		class LoginStatusCallback implements AsyncCallback<JavaScriptObject> {
			public void onSuccess ( JavaScriptObject response ) {
				System.out.println("LoginStatusCallback");
				renderApp( Window.Location.getHash() );
			}

			@Override
			public void onFailure(Throwable caught) {
				throw new RuntimeException ( caught );

			}
		}
		LoginStatusCallback loginStatusCallback = new LoginStatusCallback ();

		// Get login status
		fbCore.getLoginStatus( loginStatusCallback );
		


	}


	private void loadUserInfo() {

		userInfoService.getUserInfo(new AsyncCallback<UserInfoClient>() {
			@Override
			public void onFailure(Throwable caught) {
				System.out.println("No User Info found yet");
				userInfo = new UserInfoClient(loginInfo.getNickname(), "driver", 5.00, 0, 0);
				userInfoService.setUserInfo(userInfo, new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void result) {

					}
				});
			}

			@Override
			public void onSuccess(UserInfoClient result) {
				System.out.println("I'm at loadUserInfo");

				userInfo = result;
				initializeSliderValues();

			}
		});
	}

	private void saveCriteria() {
		double maxPrice = ((double)priceFilterSlider.getValue()/2); // Divide by two to get non-integer prices
		System.out.println(maxPrice);
		double minTime = (double)timeFilterSlider.getValue();
		double maxRadius = (double)radiusFilterSlider.getValue();

		System.out.println("window is closing. price " + maxPrice);
		userInfoService.setCriteria(maxRadius, maxPrice, minTime, userInfo, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Void result) {
				System.out.println("Saved info");
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

	private void loadSetUserType() {
		
		setUserPanel.add(setUserLabel);
		setUserPanel.add(driverButton);
		setUserPanel.add(busOwnButton);
		setUserPanel.add(adminButton);
		setUserPanel.add(setUserButton);
		RootPanel.get("parkMe").add(setUserPanel);

		// Listen for mouse events on the Set User Type button.
		setUserButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				setUserPanel.setVisible(false);
				loadParkMe();
			}
		});
	}


	public void renderFB() {
		System.out.println("renderFB");
		
		if ( fbCore.getAuthResponse() != null ) {
			//fbPanel.setVisible(false);
		//	RootPanel.get("parkMe").add(fbPanel);
		//	FBXfbml.parse();
			//loadSetUserType();
			loadParkMe();
		} else {
			//fbPanel.add( new HTML ( "This demo uses Facebook Connect. Please click to login <fb:login-button autologoutlink='true' /> " ) );
//			fbPanel.add ( new HTML ( "This app uses Facebook Connect. Please click to login " ) );
//			fbPanel.add ( new HTML ( "<fb:login-button autologoutlink='true' scope='publish_stream,read_stream,create_event' /> " ) );
//			//fbPanel.add ( new HTML ( "<hr/><fb:comments xid='gwtfb' />" ) );
			RootPanel.get("parkMe").add(fbPanel);
			FBXfbml.parse();
			//loadParkMe();
		}


	}

	private void initializeSliderValues() {

		double maxPrice = userInfo.getMaxPrice();
		double minTime = userInfo.getMinTime();
		double radius = userInfo.getRadius();

		int initMaxPrice = (int) maxPrice * 2;
		int initMinTime = (int) minTime;
		int initMaxRadius = (int) radius;

		// Set slider values:
		priceFilterSlider.setValue(initMaxPrice);
		timeFilterSlider.setValue(initMinTime);
		radiusFilterSlider.setValue(initMaxRadius);
	}

	private void initializeFlexTables() {
		showFaves();
	}

	//	private void addListenerToTabs() {
	//		tabs.addSelectionHandler(new SelectionHandler<Integer>() {
	//			public void onSelection(SelectionEvent<Integer> event) {
	//				switch (event.getSelectedItem()) {
	//				case 0: case 3:
	//					// display search results on map
	//					break;
	//				case 1:
	//					// display favourites on map
	//					break;
	//				case 2:
	//					// display parking history on map
	//					break;
	//				}
	//			}
	//		});
	//	}

	private void addListenerToResults() {
		resultsFlexTable.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int col = resultsFlexTable.getCellForEvent(event).getCellIndex();
				if (col == 1) {
					return;
				}
				int row = resultsFlexTable.getCellForEvent(event).getRowIndex();
				String parkingID = idList.get(row);
				System.out.println("I have clicked on parking with ID: "
						+ parkingID);
				// get corresponding ParkingLocation with parkingID
				parkService.getParking(parkingID,
						new AsyncCallback<ParkingLocation>() {

					@Override
					public void onFailure(Throwable caught) {

					}

					@Override
					public void onSuccess(final ParkingLocation parking) {
						if (zoom == false) {
							zoom = true;
							theMap.setZoom(17);
						}

						Button addFaveButton = new Button("Add to Faves");
						addHandler(addFaveButton, parking);

						parking.displayPopup(theMap, infoWindow, addFaveButton);

					}

				});

			}
		});


		faveFlexTable.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int col = faveFlexTable.getCellForEvent(event).getCellIndex();
				if (col == 1) {
					return;
				}
				int row = faveFlexTable.getCellForEvent(event).getRowIndex();
				String parkingID = faveList.get(row);
				System.out.println("I have clicked on fave with ID: "
						+ parkingID);
				// get corresponding ParkingLocation with parkingID
				parkService.getParking(parkingID,
						new AsyncCallback<ParkingLocation>() {

					@Override
					public void onFailure(Throwable caught) {

					}

					@Override
					public void onSuccess(final ParkingLocation parking) {
						if (zoom == false) {
							zoom = true;
							theMap.setZoom(17);
						}

						Button addFaveButton = new Button("Add to Faves");
						addHandler(addFaveButton, parking);
						parking.displayPopup(theMap, infoWindow, addFaveButton);

					}

				});

			}
		});
	}

	private void addListenersToButtons() {

		// Listen for events on the sortBox
		sortBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				tabs.selectTab(0);
				//				displayParkings(idList);
			}
		});

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
				infoWindow.close();
				mapOperator.clearCircle();
				mapOperator.clearMap();
				resultsFlexTable.removeAllRows();
				idList.clear();
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
				if (address.equals("")) {	
					infoWindow.close();
					filterParkings();
				}
				else {searchLoc(address);}
				tabs.selectTab(0);
			}
		});

		avgPriceButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				calculateAverage();

			}
		});

		downloadData.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				downloadData();

			}
		});

		setColor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				loadDataService.getParking(new AsyncCallback<ParkingLocation[]>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Error getting parking");
					}

					@Override
					public void onSuccess(ParkingLocation[] result) {
						for (ParkingLocation p : result) {
							double rate = p.getPrice();
							String color = "black";
							if (rate <= 1) {
								color = "#66CD00";
							} else if (rate <= 1.5 && rate > 1) {
								color = "#9BD500";
							} else if (rate <= 2 && rate > 1.5) {
								color = "#B7D900";
							} else if (rate <= 2.5 && rate > 2) {
								color = "#E0CF00";
							} else if (rate <= 3 && rate > 2.5) {
								color = "#E8A100";
							} else if (rate <= 3.5 && rate > 3) {
								color = "#EC8800";
							} else if (rate <= 4 && rate > 3.5) {
								color = "#F35400";
							} else if (rate <= 4.5 && rate > 4) {
								color = "#FB1D00";
							} else if (rate > 4.5) {
								color = "#FF0000";
							}


							loadDataService.setColor(color, p.getParkingID(), new AsyncCallback<Void>() {
								@Override
								public void onFailure(Throwable caught) {}

								@Override
								public void onSuccess(Void result) {}
							});
						}
					}
				});
			}
		});


	}


	protected void calculateAverage() {

		if (totalNum == 0) {
			avgPrice.setText("Please click on Download Data above first.");
		}
		else {
			String address = avgPriceAddress.getText();

			if (address.equals("") || avgPriceRadius.getText().equals("")) {
				avgPrice.setText("Please enter the values above.");
			}

			else {
				final double radius = Double.parseDouble(avgPriceRadius.getText());

				GeocoderRequest request = GeocoderRequest.create();
				request.setAddress(address + " Vancouver");
				request.setRegion("ca");
				geocoder.geocode(request, new Geocoder.Callback() {

					@Override
					public void handle(JsArray<GeocoderResult> results,
							GeocoderStatus status) {
						if (status == GeocoderStatus.OK) {
							searchResult = results;
							LatLng latlong = searchResult.get(0).getGeometry().getLocation();
							String addr = searchResult.get(0).getFormattedAddress();
							double ctrLat = latlong.lat();
							double ctrLng = latlong.lng();
							theMap.setCenter(latlong);
							infoWindow.setContent(new Label(addr));
							infoWindow.setPosition(latlong);
							infoWindow.open(theMap);

							int i = 0;
							double totalPrice = 0;
							for (ParkingLocation p : allParkings) {
								if (isInRadius(p, radius, ctrLat, ctrLng)) {
									totalPrice = totalPrice + p.getPrice();
									i++;
								}
							}
							if (i == 0) {
								avgPrice.setText("No parking locations found.");
							}
							else {
								double avg = totalPrice/i;
								NumberFormat fmt = NumberFormat.getFormat("0.00");
								String avgP = fmt.format(avg);
								avgPrice.setText("Average Price within " + Double.toString(radius) + "m of " + addr + ": $" + avgP + "/hr");
							}

						}
					}
				});
			}
		}


	}

	// Returns true if the endpoints or midpoint of the parking location are within radius metres of point
	private boolean isInRadius(ParkingLocation p, Double radius, double ctrLat, double ctrLng) {
		double startlat = p.getStartLat();
		double startlong = p.getStartLong();
		double endlat = p.getEndLat();
		double endlong = p.getEndLong();
		return (distance(startlat, startlong, ctrLat, ctrLng) <= radius
				|| distance(endlat, endlong, ctrLat, ctrLng) <= radius
				|| distance((startlat + endlat)/2, (startlong + endlong)/2, ctrLat, ctrLng) <= radius);
	}

	// Returns the distance between two points in metres, given their lats and longs
	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + 
				Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
		dist = Math.acos(dist);
		dist = Math.toDegrees(dist);
		dist = dist * 60 * 1151.5;
		dist = dist * 1.609344;
		//System.out.println(dist);
		return dist;
	}


	private void addListenersToSliders() {

		// Update max price value label when slider moves
		priceFilterSlider.addBarValueChangedHandler(new BarValueChangedHandler() {
			public void onBarValueChanged(BarValueChangedEvent event) {

				double maxPrice = ((double)event.getValue())/2; // Divide by two to get non-integer prices
				String formatted = NumberFormat.getFormat("#0.00").format(maxPrice);
				maxPriceValueLabel.setText("$" + formatted + " / hr");
				filterParkings();
				saveCriteria();
			}
		});

		// Update min time value label when slider moves
		timeFilterSlider.addBarValueChangedHandler(new BarValueChangedHandler() {
			public void onBarValueChanged(BarValueChangedEvent event) {
				minTimeValueLabel.setText(event.getValue() + " hrs");
				filterParkings();
				saveCriteria();
			}
		});

		// Update max radius value label when slider moves
		radiusFilterSlider.addBarValueChangedHandler(new BarValueChangedHandler() {
			public void onBarValueChanged(BarValueChangedEvent event) {
				maxRadiusValueLabel.setText(event.getValue() + " m");
				filterParkings();
				saveCriteria();
			}
		});
	}

	private void createMap() {
		// Set up map options
		MapOptions options = MapOptions.create();
		options.setCenter(LatLng.create(49.251, -123.119));
		options.setZoom(defaultZoom);
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

		// Set up filterPanel
		filterPanel.setSize("450px", "100px");
		filterPanel.addStyleName("filterPanel");
		filterPanel.add(maxPriceLabel, 1, 10);
		filterPanel.add(minTimeLabel, 1, 40);
		filterPanel.add(maxRadiusLabel, 1, 70);
		filterPanel.add(priceFilterSlider, 130, 15);
		filterPanel.add(timeFilterSlider, 130, 45);
		filterPanel.add(radiusFilterSlider, 130, 75);
		filterPanel.add(maxPriceValueLabel, 350, 10);
		filterPanel.add(minTimeValueLabel, 350, 40);
		filterPanel.add(maxRadiusValueLabel, 350, 70);

		// Set up sortBox
		sortBox.addItem("Price");
		sortBox.addItem("Time Limit");
		sortBox.addItem("Distance");
		sortBox.setVisibleItemCount(1);
		sortPanel.add(sortLabel);
		sortPanel.add(sortBox);

		// Set up searchPanel
		searchBox.setHeight("1em");
		//searchPanel.add(searchLabel);
		searchPanel.add(searchBox);
		searchPanel.add(searchButton);
		searchPanel.add(clearDataButton);
		searchPanel.add(signOutLink);

		searchLabel.setText("Enter Address (or leave blank to search whole Vancouver):");

		// ADMIN CONTROLS:
		//  tabPanel.add(loadDataButton);
		//  tabPanel.add(getAddressesButton);
		//  tabPanel.add(setColor);
		// tabPanel.add(displayDataButton);
		// tabPanel.add(clearDataButton);
		//  tabPanel.add(filterButton);
		//  tabPanel.add(downloadData);
		// tabPanel.add(signOutLink);

		resultsFlexTable.setCellPadding(5);
		faveFlexTable.setCellPadding(5);
		histFlexTable.setCellPadding(5);

		resultsScroll.add(resultsFlexTable);
		faveScroll.add(faveFlexTable);
		histScroll.add(histFlexTable);

		//mainHorzPanel.add(resultsScroll);

		flowpanel = new FlowPanel();
		flowpanel.add(resultsScroll);
		tabs.add(flowpanel, "Results");

		flowpanel = new FlowPanel();
		flowpanel.add(faveScroll);
		tabs.add(flowpanel, "Favorites");

		flowpanel = new FlowPanel();
		flowpanel.add(histScroll);
		tabs.add(flowpanel, "History");

		// set up statistics tab

		avgPriceVP.add(avgPriceLabel);
		avgPriceVP.add(avgPriceAddress);
		avgPriceVP.add(avgPriceRadiusLbl);
		avgPriceVP.add(avgPriceRadius);
		avgPriceVP.add(avgPriceButton);
		avgPriceVP.add(avgPrice);
		statsScroll.add(avgPriceVP);
		flowpanel = new FlowPanel();
		flowpanel.add(statsScroll);
		tabs.add(flowpanel, "Statistics");

		tabs.selectTab(0);

		// Put together main panels
		
		leftVertPanel.add(searchLabel);
		leftVertPanel.add(searchPanel);
		leftVertPanel.add(filterPanel);
		leftVertPanel.add(sortPanel);
		leftVertPanel.add(tabs);
		mainPanel.add(leftVertPanel);
		mainPanel.add(rightVertPanel);

		// Set sizes for elements

		String scrollHeight = Window.getClientHeight() - 265 + "px";
		String scrollWidth = 0.4 * Window.getClientWidth() - 60 + "px";
		resultsScroll.setSize(scrollWidth, scrollHeight);
		faveScroll.setSize(scrollWidth, scrollHeight);
		histScroll.setSize(scrollWidth, scrollHeight);
		statsScroll.setSize(scrollWidth, scrollHeight);
		tabs.setSize(0.4 * Window.getClientWidth() - 50 + "px", "100%");

		resultsFlexTable.setSize(scrollWidth, "100%");
		faveFlexTable.setSize(scrollWidth, "100%");
		histFlexTable.setSize(scrollWidth, "100%");
		leftVertPanel.setSize(0.3 * Window.getClientWidth() + "px", "100%");
		rightVertPanel.setSize(0.7 * Window.getClientWidth() - 120 + "px", Window.getClientHeight() - 20 + "px");

		mapPanel.setSize("100%", "100%");
		mainPanel.setSpacing(0);
		mainPanel.setSize("100%", "100%");

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
		//		ParkingLocation[] parkingLoc = allParkings.toArray(new ParkingLocation[totalNum]);
		displayParkings(allParkings);

		/**
		 * server side 
		 * 
		loadDataService.getParking(new AsyncCallback<ParkingLocation[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error getting parking");
			}

			@Override
			public void onSuccess(ParkingLocation[] result) {
				resultsFlexTable.removeAllRows();
				idList.clear();
				//mapOperator.drawLocs(result, infoWindow);
				displayParkings(result);
				// Window.alert("Successfully displayed data");
			}

		});
		 **/

	}

	private void displayParkings(List<ParkingLocation> parkingLocations) {
		mapOperator.clearMap();
		resultsFlexTable.removeAllRows();
		idList.clear();

		if (parkingLocations.size() == 0) {
			resultsFlexTable.setText(0, 0, "No results found.");
		} else {

			int index = sortBox.getSelectedIndex();
			parkingLocations = sortBy(sortBox.getItemText(index), parkingLocations);

			for (ParkingLocation p : parkingLocations) {
				displayParking(p);
			}
		}
	}

	private void displayParking(final ParkingLocation parkingLoc) {

		VerticalPanel info = new VerticalPanel();

		// Exception in this line when I try to display all data:
		// this exception only seems to occur on development mode-- and not appengine.
		HTML street = new HTML("<b>" + parkingLoc.getStreet() + "</b>");
		HTML rate = new HTML("<u>Rate:</u> $" + parkingLoc.getPrice() + "/hr");
		HTML limit = new HTML("<u>Limit:</u> " + parkingLoc.getLimit() + "hr/s");
		info.add(street);
		info.add(rate);
		info.add(limit);
		int row = resultsFlexTable.getRowCount();

		if (parkingLoc.getColor().equals("#66CD00")) {
			resultsFlexTable.getRowFormatter().addStyleName(row, "parking1");
		} else if (parkingLoc.getColor().equals("#9BD500")) {
			resultsFlexTable.getRowFormatter().addStyleName(row, "parking2");
		} else if (parkingLoc.getColor().equals("#B7D900")) {
			resultsFlexTable.getRowFormatter().addStyleName(row, "parking3");
		} else if (parkingLoc.getColor().equals("#E0CF00")) {
			resultsFlexTable.getRowFormatter().addStyleName(row, "parking4");
		} else if (parkingLoc.getColor().equals("#E8A100")) {
			resultsFlexTable.getRowFormatter().addStyleName(row, "parking5");
		} else if (parkingLoc.getColor().equals("#EC8800")) {
			resultsFlexTable.getRowFormatter().addStyleName(row, "parking6");
		} else if (parkingLoc.getColor().equals("#F35400")) {
			resultsFlexTable.getRowFormatter().addStyleName(row, "parking7");
		} else if (parkingLoc.getColor().equals("#FB1D00")) {
			resultsFlexTable.getRowFormatter().addStyleName(row, "parking8");
		} else if (parkingLoc.getColor().equals("#FF0000")) {

			resultsFlexTable.getRowFormatter().addStyleName(row, "parking9");
		}
		resultsFlexTable.setWidget(row, 0, info);

		Button addFaveButton = new Button("Add to Faves");

		addHandler(addFaveButton, parkingLoc);

		mapOperator.drawOnMap(parkingLoc, infoWindow, addFaveButton);
		idList.add(parkingLoc.getParkingID());
		System.out.println("Currently printing parking " + parkingLoc.getParkingID());
	}

	private void filterParkings() {
		LatLng searchPoint;

		double maxPrice = ((double)priceFilterSlider.getValue()/2); // Divide by two to get non-integer prices
		System.out.println(maxPrice);
		double minTime = (double)timeFilterSlider.getValue();
		double maxRadius;

		if (searchBox.getText().equals("")) {
			System.out.println("Centering it to downtown");
			searchPoint = LatLng.create(49.2814,-123.12);
			maxRadius = 99999999;
			mapOperator.clearCircle();

		} else {
			searchPoint = searchResult.get(0).getGeometry().getLocation();
			System.out.println("Filtering for results around " + searchResult.get(0).getFormattedAddress());
			maxRadius = (double)radiusFilterSlider.getValue();
			mapOperator.drawCircle(searchPoint, maxRadius);

		}
		/**
		 * 
		 * client side filtering 
		 * 
		 **/
		System.out.println("Filtering with maxPrice = " + maxPrice + " and minTime = " + minTime + " and maxRadius = " + maxRadius);

		List<ParkingLocation> filtered = new ArrayList<ParkingLocation>();
		for (int i = 0; i < totalNum; i++) {
			ParkingLocation p = allParkings.get(i);
			if ((p.getPrice() <= maxPrice) && (p.getLimit() >= minTime) && isInRadius(p, maxRadius, searchPoint.lat(), searchPoint.lng())) {
				filtered.add(p);
			}
			System.out.println("Found " + filtered.size() + " locations");
		}

		displayParkings(filtered);

		/**
		 * 
		 * server side filtering
		 * 
		Criteria crit = new Criteria(maxRadius, maxPrice, minTime, searchPoint.lat(), searchPoint.lng());
		filterService.getParking(crit, new AsyncCallback<ParkingLocation[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error getting parking");
			}

			@Override
			public void onSuccess(ParkingLocation[] result) {
				// Window.alert("Successfully displayed filtered data");

				int length = result.length;
				System.out.println("Found " + length + " results matching criteria");
				mapOperator.clearMap();
				resultsFlexTable.removeAllRows();
				idList.clear();
				if (length == 0) {
					resultsFlexTable.setText(0, 0, "No results found.");
				} else {
					displayParkings(result);
				}
			}
		});

		 **/
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

			GeocoderRequest request = GeocoderRequest.create();
			request.setLocation(latlong);
			geocoder.geocode(request, new Geocoder.Callback() {
				@Override
				public void handle(JsArray<GeocoderResult> results,
						GeocoderStatus status) {
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
									}

									@Override
									public void onSuccess(Void result) {
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
							}
							@Override
							public void onSuccess(Void result) {
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

	private void searchLoc(final String address) {
		GeocoderRequest request = GeocoderRequest.create();
		request.setAddress(address + " Vancouver");
		request.setRegion("ca");
		geocoder.geocode(request, new Geocoder.Callback() {

			@Override
			public void handle(JsArray<GeocoderResult> results,
					GeocoderStatus status) {
				if (status == GeocoderStatus.OK) {
					Button createEvent = new Button("Create Event");
					Button getDirections = new Button("Directions to Here");
					searchResult = results;
					final LatLng latlong = searchResult.get(0).getGeometry().getLocation();
					final String addr = searchResult.get(0).getFormattedAddress();
					theMap.setCenter(latlong);
					VerticalPanel main = new VerticalPanel();
					HorizontalPanel buttons = new HorizontalPanel();
					main.add(new Label(addr));
					buttons.add(createEvent);
					buttons.add(getDirections);
					main.add(buttons);

					createEvent.addClickHandler(new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							// TODO create popup asking for event title & date
							
							JSONObject param = new JSONObject();
							param.put("name", new JSONString("ParkMe Sample Event"));
							param.put("start_time", new JSONString("2012-12-12"));
							param.put("location", new JSONString(addr));
							param.put("description", new JSONString("This event was automatically generated by the ParkMe app."));
							fbCore.api("/me/events", "post", param.getJavaScriptObject(), new AsyncCallback<JavaScriptObject>() {
								@Override
								public void onFailure(Throwable caught) {
								}

								@Override
								public void onSuccess(JavaScriptObject result) {
									
									JSONObject res = new JSONObject(result);
									String id = res.get("id").toString();
									Window.alert("Created new Facebook Event with id " + id);
									
								}
							});

						}
					});

					getDirections.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							// TODO get directions
							System.out.println("I have clicked on get directions");

							DirectionsService ds = DirectionsService.create();
							DirectionsRequest dr = DirectionsRequest.create();
							final DirectionsRenderer displayDir = DirectionsRenderer.create();

							dr.setDestination(latlong);
							// currently, set origin to UBC.
							LatLng ubc = LatLng.create(49.2661156, -123.2457198);
							// TODO: get origin, either by asking for a location or using GPS
							dr.setOrigin(ubc);
							dr.setTravelMode(TravelMode.DRIVING);
							ds.route(dr, new DirectionsService.Callback() {

								@Override
								public void handle(DirectionsResult result, DirectionsStatus status) {
									System.out.println("Getting directions");
									if (status.equals(DirectionsStatus.OK)) {
										displayDir.setMap(theMap);
										displayDir.setDirections(result);
										// TODO: add need info panel for instructions
									}

								}
							});
						}
					});


					infoWindow.setContent(main);
					infoWindow.setPosition(latlong);
					infoWindow.open(theMap);
					theMap.setZoom(17);
					filterParkings();
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

	private void displayFavorites(ParkingLocation[] parkingLocs) {
		mapOperator.clearMap();
		for (ParkingLocation p : parkingLocs) {
			displayFavorite(p);
		}
	}

	private void displayFavorite(final ParkingLocation parkingLoc) {

		final VerticalPanel info = new VerticalPanel();
		HTML street = new HTML("<b>" + parkingLoc.getStreet() + "</b>");
		HTML rate = new HTML("<u>Rate:</u> $" + parkingLoc.getPrice() + "/hr");
		HTML limit = new HTML("<u>Limit:</u> " + parkingLoc.getLimit() + "hr/s"); 
		info.add(street);
		info.add(rate);
		info.add(limit);
		final int row = faveFlexTable.getRowCount();
		if (parkingLoc.getColor().equals("#66CD00")) {
			faveFlexTable.getColumnFormatter().setWidth(1, "30 px");
			faveFlexTable.getCellFormatter().addStyleName(row, 0, "parking1");
		} else if (parkingLoc.getColor().equals("#9BD500")) {
			faveFlexTable.getColumnFormatter().setWidth(1, "30 px");
			faveFlexTable.getCellFormatter().addStyleName(row, 0, "parking2");
		} else if (parkingLoc.getColor().equals("#B7D900")) {
			faveFlexTable.getColumnFormatter().setWidth(1, "30 px");
			faveFlexTable.getCellFormatter().addStyleName(row, 0, "parking3");
		} else if (parkingLoc.getColor().equals("#E0CF00")) {
			faveFlexTable.getColumnFormatter().setWidth(1, "30 px");
			faveFlexTable.getCellFormatter().addStyleName(row, 0, "parking4");
		} else if (parkingLoc.getColor().equals("#E8A100")) {
			faveFlexTable.getColumnFormatter().setWidth(1, "30 px");
			faveFlexTable.getCellFormatter().addStyleName(row, 0, "parking5");
		} else if (parkingLoc.getColor().equals("#EC8800")) {
			faveFlexTable.getColumnFormatter().setWidth(1, "30 px");
			faveFlexTable.getCellFormatter().addStyleName(row, 0, "parking6");
		} else if (parkingLoc.getColor().equals("#F35400")) {
			faveFlexTable.getColumnFormatter().setWidth(1, "30 px");
			faveFlexTable.getCellFormatter().addStyleName(row, 0, "parking7");
		} else if (parkingLoc.getColor().equals("#FB1D00")) {
			faveFlexTable.getColumnFormatter().setWidth(1, "30 px");
			faveFlexTable.getCellFormatter().addStyleName(row, 0, "parking8");
		} else if (parkingLoc.getColor().equals("#FF0000")) {
			faveFlexTable.getColumnFormatter().setWidth(1, "30 px");
			faveFlexTable.getCellFormatter().addStyleName(row, 0, "parking9");
		}
		faveFlexTable.setWidget(row, 0, info);
		Button removeFaveButton = new Button("Remove");

		removeFaveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				removeFave(parkingLoc.getParkingID());
			}
		});
		faveFlexTable.setWidget(row, 1, removeFaveButton);
		final Button addFaveButton = new Button("Add to Faves");

		addHandler(addFaveButton,parkingLoc);

		mapOperator.drawOnMap(parkingLoc, infoWindow, addFaveButton);
		faveList.add(parkingLoc.getParkingID());
		System.out.println("Currently printing parking " + parkingLoc.getParkingID());

	}

	private void addHandler(Button addFaveButton, final ParkingLocation parkingLoc) {
		addFaveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				FaveAsync fave = GWT.create(Fave.class);
				fave.addFave(parkingLoc.getParkingID(), new AsyncCallback<Void>() {

					@Override
					public void onSuccess(Void result) {
						addFaveToDisplay(parkingLoc);
					}

					@Override
					public void onFailure(Throwable caught) {
					}
				});
			}
		});
	}

	private void removeFave(final String parkingID) {
		fave.removeFave(parkingID, new AsyncCallback<Void>() {

			public void onFailure(Throwable error) {
				handleError(error);
			}
			public void onSuccess(Void ignore) {
				undisplayFave(parkingID);
			}

		});
	}

	private void undisplayFave(String parkingID) {
		int removedIndex = faveList.indexOf(parkingID);
		faveList.remove(removedIndex);        
		faveFlexTable.removeRow(removedIndex);
		if (faveFlexTable.getRowCount() == 0) {
			faveFlexTable.setText(0, 0, "You haven't added anything to favorites yet.");
		}
	}

	private void handleError(Throwable error) {
		Window.alert(error.getMessage());
		if (error instanceof NotLoggedInException) {
			Window.Location.replace(loginInfo.getLogoutUrl());
		}
	}

	public void showFaves() {
		fave.getFaves(new AsyncCallback<String[]>() {

			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(String[] result) {
				faveFlexTable.removeAllRows();
				idList.clear();
				if (result.length == 0) {
					faveFlexTable.setText(0, 0, "You haven't added anything to favorites yet.");

				}

				else {
					parkService.getParkings(result, new AsyncCallback<ParkingLocation[]>() {

						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(ParkingLocation[] result) {
							displayFavorites(result);
						}
					});}
			}
		});
	}

	private void addFaveToDisplay(ParkingLocation parkingLoc) {
		if (faveList.size() == 0) {
			faveFlexTable.removeAllRows();
		}
		displayFavorite(parkingLoc);

	}

	// Sort the list of parking locations by price, time limit, or distance
	private List<ParkingLocation> sortBy(String sortMode, List<ParkingLocation> parkingLocations) {
		Comparator<ParkingLocation> c = getComparator(sortMode);
		Collections.sort(parkingLocations, c);
		return parkingLocations;
	}

	private Comparator<ParkingLocation> getComparator(String sortParam) {
		if ("Price".equals(sortParam)) {
			return new Comparator<ParkingLocation>() {
				@Override
				public int compare(ParkingLocation o1, ParkingLocation o2) {
					return new Double(o1.getPrice()).compareTo(new Double(o2.getPrice()));
				}
			};
		} else if ("Time Limit".equals(sortParam)) {
			return new Comparator<ParkingLocation>() {
				@Override
				public int compare(ParkingLocation o1, ParkingLocation o2) {
					return new Double(o1.getLimit()).compareTo(new Double(o2.getLimit()));
				}
			};
		} else if ("Distance".equals(sortParam)) {
			return new Comparator<ParkingLocation>() {
				@Override
				public int compare(ParkingLocation o1, ParkingLocation o2) {
					// TODO Auto-generated method stub
					return 0;
				}
			};
		} else {
			throw new IllegalArgumentException("invalid sort field '" + sortParam + "'");
		}
	}


	private void renderApp ( String token ) {

		System.out.println(token);
		token = token.replace("#", "");

		if ( token == null || "".equals ( token ) || "#".equals ( token ) ) 
		{
			token = "home";
		}

		if ( token.endsWith("home") ) {
			renderFB ();

		} else {
			Window.alert ( "Unknown  url "  + token );
		}
	}


	 public void onValueChange(ValueChangeEvent<String> event) {
	        renderApp ( event.getValue() );
	    }

}
