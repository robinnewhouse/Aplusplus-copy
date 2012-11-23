package ca.ubc.cpsc310.parkme.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ca.ubc.cpsc310.parkme.client.sdk.FBCore;
import ca.ubc.cpsc310.parkme.client.sdk.FBEvent;
import ca.ubc.cpsc310.parkme.client.sdk.FBXfbml;
import ca.ubc.cpsc310.parkme.client.services.history.SearchHistoryOrganizer;
import ca.ubc.cpsc310.parkme.client.services.parking.Fave;
import ca.ubc.cpsc310.parkme.client.services.parking.FaveAsync;
import ca.ubc.cpsc310.parkme.client.services.parking.FilterService;
import ca.ubc.cpsc310.parkme.client.services.parking.FilterServiceAsync;
import ca.ubc.cpsc310.parkme.client.services.parking.LoadDataService;
import ca.ubc.cpsc310.parkme.client.services.parking.LoadDataServiceAsync;
import ca.ubc.cpsc310.parkme.client.services.parking.ParkingLocService;
import ca.ubc.cpsc310.parkme.client.services.parking.ParkingLocServiceAsync;
import ca.ubc.cpsc310.parkme.client.services.parking.ParkingLocation;
import ca.ubc.cpsc310.parkme.client.services.parking.ParkingStats;
import ca.ubc.cpsc310.parkme.client.services.parking.TicketInfo;
import ca.ubc.cpsc310.parkme.client.services.parking.TicketService;
import ca.ubc.cpsc310.parkme.client.services.parking.TicketServiceAsync;
import ca.ubc.cpsc310.parkme.client.services.user.Criteria;
import ca.ubc.cpsc310.parkme.client.services.user.LoginInfo;
import ca.ubc.cpsc310.parkme.client.services.user.LoginService;
import ca.ubc.cpsc310.parkme.client.services.user.LoginServiceAsync;
import ca.ubc.cpsc310.parkme.client.services.user.NotLoggedInException;
import ca.ubc.cpsc310.parkme.client.services.user.UserInfoClient;
import ca.ubc.cpsc310.parkme.client.services.user.UserInfoService;
import ca.ubc.cpsc310.parkme.client.services.user.UserInfoServiceAsync;

import com.google.code.gwt.geolocation.client.Coordinates;
import com.google.code.gwt.geolocation.client.Geolocation;
import com.google.code.gwt.geolocation.client.Position;
import com.google.code.gwt.geolocation.client.PositionCallback;
import com.google.code.gwt.geolocation.client.PositionError;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SuggestBox;
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
import com.google.maps.gwt.client.Marker;
import com.google.maps.gwt.client.MouseEvent;
import com.google.maps.gwt.client.TravelMode;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ParkMe implements EntryPoint, ValueChangeHandler<String> {

	List<String> adminEmails = new ArrayList<String>();
	Anchor signOutLink = new Anchor("Sign Out");
	UserInfoClient userInfo = new UserInfoClient();

	// FB EVENT POPUP
	private PopupPanel popUp = new PopupPanel();
	private VerticalPanel mainPan = new VerticalPanel();
	private TextBox eventName = new TextBox();
	private TextBox eventTime = new TextBox();
	private Button eventCreate = new Button("Create Event");
	private Button eventCancel = new Button("Cancel");
	private HorizontalPanel buttonPanel = new HorizontalPanel();
	private PopupPanel loadingPage = new PopupPanel();

	// DIRECTIONS
	private DirectionsService ds = DirectionsService.create();
	private DirectionsRequest dr = DirectionsRequest.create();
	private final DirectionsRenderer displayDir = DirectionsRenderer.create();

	private VerticalPanel dummy = new VerticalPanel();

	// FACEBOOK EVENT STUFF

	private String apiKey;

	private FBCore fbCore = GWT.create(FBCore.class);
	private FBEvent fbEvent = GWT.create(FBEvent.class);
	private VerticalPanel fbPanel = new VerticalPanel();

	private boolean status = true;
	private boolean xfbml = true;
	private boolean cookie = true;

	private String usertype;
	// TABPANEL
	private TabPanel tabs = new TabPanel();
	private FlowPanel flowpanel;

	private TextBox fine = new TextBox();

	// SET USER TYPE
	private VerticalPanel setUserPanel = new VerticalPanel();
	private Label setUserLabel = new Label(
			"Please select what type of user you are:");
	private RadioButton driverButton = new RadioButton("userTypes", "Driver");
	private RadioButton busOwnButton = new RadioButton("userTypes",
			"Business Owner");
	private RadioButton adminButton = new RadioButton("userTypes",
			"Administrator");
	private Button setUserButton = new Button("Continue");

	// FAVORITES, RESULTS & HISTORY
	private List<String> faveList = new ArrayList<String>();
	private List<String> ticketList = new ArrayList<String>();
	private List<String> idList = new ArrayList<String>();
	private ScrollPanel resultsScroll = new ScrollPanel();
	private ScrollPanel faveScroll = new ScrollPanel();
	private ScrollPanel histScroll = new ScrollPanel();
	private ScrollPanel ticketScroll = new ScrollPanel();
	private FlexTable resultsFlexTable = new FlexTable();
	private FlexTable faveFlexTable = new FlexTable();
	private FlexTable histFlexTable = new FlexTable();
	private FlexTable ticketFlexTable = new FlexTable();
	private final Button clearHistoryButton = new Button("Clear History");

	// STATISTICS
	private ScrollPanel statsScroll = new ScrollPanel();
	private VerticalPanel mainStatsVP = new VerticalPanel();
	private ScrollPanel dirScroll = new ScrollPanel();
	private VerticalPanel avgCritVP = new VerticalPanel();
	private FlexTable faveStatsFT = new FlexTable();
	private FlexTable ticketStatsFT = new FlexTable();
	private ScrollPanel ticketscroll = new ScrollPanel();
	private List<String> faveStatsList = new ArrayList<String>();
	private List<String> ticketStatsList = new ArrayList<String>();

	// average price
	private VerticalPanel avgPriceVP = new VerticalPanel();
	private Label avgPriceLabel = new Label(
			"Enter an address to calculate the average price around that location:");
	private TextBox avgPriceAddress = new TextBox();
	private Label avgPriceRadiusLbl = new Label("Radius: ");
	private TextBox avgPriceRadius = new TextBox();
	private Button avgPriceButton = new Button("Calculate Average");
	private Label avgPrice = new Label("");

	// GEOCODER
	private Geocoder geocoder = Geocoder.create();
	private MyInfoWindow infoWindow = MyInfoWindow.create(0L);
	private boolean zoom = false;
	private String fAddress;

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
	private Button clearDataButton = new Button("Clear Results");
	private HorizontalPanel mainPanel = new HorizontalPanel();
	private Button filterButton = new Button("Filter Results");
	private Button downloadData = new Button("Download Data to Client");

	// MAP
	private MapOperater mapOperator;
	private GoogleMap theMap;

	private double defaultZoom = 14;

	// MAIN PANELS
	private VerticalPanel leftVertPanel = new VerticalPanel();
	private AbsolutePanel mapPanel = new AbsolutePanel();
	private VerticalPanel rightVertPanel = new VerticalPanel(); // delete this
	private Label titleLabel = new Label("Park Me");

	// SEARCHING
	private HorizontalPanel searchPanel = new HorizontalPanel();
	private final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
	private final SuggestBox searchBox = new SuggestBox(oracle);
	private final SearchHistoryOrganizer searchHistoryOrganizer = new SearchHistoryOrganizer(
			histFlexTable, oracle);
	private Label searchLabel = new Label("Enter Address: ");
	private Button searchButton = new Button("Search");

	private List<ParkingLocation> allParkings = new ArrayList<ParkingLocation>();
	private List<ParkingLocation> filteredParkings = new ArrayList<ParkingLocation>();
	private int totalNum = 0;

	// The most recent location searched for
	private JsArray<GeocoderResult> searchResult;

	private final LoadDataServiceAsync loadDataService = GWT
			.create(LoadDataService.class);
	private final FilterServiceAsync filterService = GWT
			.create(FilterService.class);
	private final ParkingLocServiceAsync parkService = GWT
			.create(ParkingLocService.class);
	private final FaveAsync fave = GWT.create(Fave.class);
	private final UserInfoServiceAsync userInfoService = GWT
			.create(UserInfoService.class);
	private final TicketServiceAsync ticketService = GWT
			.create(TicketService.class);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		adminEmails.add("alyanna@alyannauy.com");
		adminEmails.add("franceskrussell@gmail.com");
		adminEmails.add("robinnewhouse@gmail.com");
		adminEmails.add("wbkdef@gmail.com");
		LoginServiceAsync loginService = GWT.create(LoginService.class);
		loginService.login(GWT.getHostPageBaseURL(),
				new AsyncCallback<LoginInfo>() {
			public void onFailure(Throwable error) {
				handleError(error);
			}

			public void onSuccess(LoginInfo result) {
				loginInfo = result;
				if (!loginInfo.isLoggedIn()) {
					loadLogin();
					/**
					 * } else if (true) { loadUserInfo();
					 * //loadFacebook(); //loadSetUserType();
					 **/
				} else {
					loadUserInfo();
					// loadParkMe();
				}
			}
		});
	}

	private void loadParkMe() {

		createMap();
		addListenersToButtons();
		addListenersToFlexTables(); // replaces addListenerToResults();
		addListenerToSortBox();
		// addListenerToTabs();

		// initializeSliderValues();
		// TODO: uncomment

		initializeLoadingPage();
		initializeDriverLayout();
		downloadData(); // initializeFlexTables(); is called on success

		// displayData();

		addListenersToSliders();
		addListenerToMarker();

	}

	private void initializeLoadingPage() {
		Double dleft = 0.24 * Window.getClientWidth();
		int left = dleft.intValue();
		Double dtop = 0.25 * Window.getClientHeight();
		int top = dtop.intValue();
		loadingPage.setSize(Window.getClientWidth() * 0.5 + "px",
				Window.getClientHeight() * 0.4 + "px");
		loadingPage.setPopupPosition(left, top);
		loadingPage.setStyleName("loading");
		HTML page = new HTML(
				"<br><br><center>Please wait while ParkMe is loading<br><br><img src=\"http://i.imgur.com/NXS4H.gif\"></center>");
		loadingPage.add(page);

	}

	private void loadFacebook(final String type) {

		if (GWT.isProdMode()) {
			apiKey = "464072253644385";
		} else {
			apiKey = "219605264787363";
		}

		fbCore.init(apiKey, status, cookie, xfbml);
		System.out.println("load facebook");
		fbPanel.setStyleName("fb");
		fbPanel.add(new HTML(
				"This app uses Facebook Connect. Please click to login "));
		fbPanel.add(new HTML(
				"<fb:login-button autologoutlink='true' scope='publish_stream,read_stream,create_event' /> "));
		fbPanel.add(new HTML(
				"<hr/><div id=\"comments\"><fb:comments xid='gwtfb' numposts='3' publish_feed='true'/></div>"));
		class SessionChangeCallback implements AsyncCallback<JavaScriptObject> {
			public void onSuccess(JavaScriptObject response) {
				System.out.println("SessionChangeCallback");
				renderFB(type);
			}

			@Override
			public void onFailure(Throwable caught) {
				throw new RuntimeException(caught);
			}
		}

		SessionChangeCallback sessionChangeCallback = new SessionChangeCallback();
		fbEvent.subscribe("auth.sessionChange", sessionChangeCallback);
		fbEvent.subscribe("auth.ResponseChange", sessionChangeCallback);
		fbEvent.subscribe("auth.login", sessionChangeCallback);
		fbEvent.subscribe("auth.logout", sessionChangeCallback);
		// renderFB();

		class LoginStatusCallback implements AsyncCallback<JavaScriptObject> {
			public void onSuccess(JavaScriptObject response) {
				System.out.println("LoginStatusCallback");

				renderApp(Window.Location.getHash(), type);
			}

			@Override
			public void onFailure(Throwable caught) {
				throw new RuntimeException(caught);

			}
		}
		LoginStatusCallback loginStatusCallback = new LoginStatusCallback();

		// Get login status
		fbCore.getLoginStatus(loginStatusCallback);

	}

	private void loadUserInfo() {

		userInfoService.getUserInfo(new AsyncCallback<UserInfoClient>() {
			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(UserInfoClient result) {
				if (result == null) {
					// no user info yet;
					System.out
					.println("Don't have a user info stored (result is null)");
					userInfo = result;

					loadSetUserType();
				} else {

					System.out.println("I'm at loadUserInfo");

					userInfo = result;

					userInfoService.getType(userInfo,
							new AsyncCallback<String>() {

						@Override
						public void onFailure(Throwable caught) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onSuccess(String type) {
							System.out.println(type);
							if (type.equals(null)) {
								loadSetUserType();
							} else {
								loadFacebook(type);
								// initializeSliderValues();
							}
						}
					});

				}

			}
		});
	}

	private void saveCriteria() {
		double maxPrice = ((double) priceFilterSlider.getValue() / 2); // Divide
		double minTime = (double) timeFilterSlider.getValue();
		double maxRadius = (double) radiusFilterSlider.getValue();

		userInfoService.setCriteria(maxRadius, maxPrice, minTime, userInfo,
				new AsyncCallback<Void>() {
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
		VerticalPanel loginPanel = new VerticalPanel();
		loginPanel.setSize(Window.getClientWidth() + "px",
				Window.getClientHeight() - 30 + "px");
		loginPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		RootPanel.get("parkMe").add(loginPanel);

		loginPanel.add(new HTML("<H1>Park Me</H1>"));

		loginPanel.add(new HTML(
				"<H2>The Perfect Spot is just around the corner</H2>"));

		Image car = new Image();
		car.setUrl("/images/car.jpg");
		loginPanel.add(car);


		Label loginLabel = new Label(
				"Please sign in to your Google Account to access the ParkMe application.");
		loginPanel.add(loginLabel);

		Anchor signInLink = new Anchor("Sign In");
		signInLink.setSize("2em", "2em");
		signInLink.setHref(loginInfo.getLoginUrl());
		loginPanel.add(signInLink);
	}

	private void loadSetUserType() {
		VerticalPanel mainUserPanel = new VerticalPanel();
		mainUserPanel.setStyleName("usertypeBG");
		mainUserPanel.setSize(Window.getClientWidth() + "px",
				Window.getClientHeight() - 30 + "px");
		mainUserPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		mainUserPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

		setUserPanel.setStyleName("usertype");
		signOutLink.setHref(loginInfo.getLogoutUrl());

		setUserPanel.add(setUserLabel);
		setUserPanel.add(driverButton);

		setUserPanel.add(adminButton);
		setUserPanel.add(setUserButton);
		setUserPanel.add(signOutLink);
		mainUserPanel.add(setUserPanel);
		// RootPanel.get("parkMe").add(setUserPanel);
		RootPanel.get("parkMe").add(mainUserPanel);
		// Listen for mouse events on the Set User Type button.
		setUserButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				if (driverButton.getValue()) {
					usertype = "driver";
				} else if (adminButton.getValue()
						&& adminEmails.contains(loginInfo.getEmailAddress())) {
					usertype = "admin";
				} else if (adminButton.getValue()) {
					Window.alert("Only specified users can be admin.");
					return;
				} else {
					Window.alert("Please select one of the choices above.");
					return;
				}
				setUserPanel.setVisible(false);
				if (userInfo == null) {
					userInfo = new UserInfoClient(loginInfo.getNickname(),
							usertype, 5.00, 0, 0);
				} else {
					userInfo.setUserType(usertype);
				}
				userInfoService.setUserInfo(userInfo,
						new AsyncCallback<Void>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Void result) {
						// loadParkMe();
						// loadCorrectPage(usertype);
						loadFacebook(usertype);
					}
				});

			}
		});
	}

	public void renderFB(String type) {
		System.out.println("renderFB");

		if (fbCore.getAuthResponse() != null) {
			loadCorrectPage(type);
		} else {
			RootPanel.get("parkMe").clear();
			VerticalPanel fbBG = new VerticalPanel();
			fbBG.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
			fbBG.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
			fbBG.setSize(Window.getClientWidth() + "px",
					Window.getClientHeight() - 30 + "px");
			fbBG.setStyleName("usertypeBG");
			fbBG.add(fbPanel);
			RootPanel.get("parkMe").add(fbBG);
			FBXfbml.parse();
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

		// Set slider text:
		String formatted = NumberFormat.getFormat("#0.00").format(maxPrice);
		maxPriceValueLabel.setText("$" + formatted + " / hr");
		minTimeValueLabel.setText(minTime + " hrs");
		maxRadiusValueLabel.setText(radius + " m");
	}

	private void initializeFlexTables() {

		showFaves();
		searchHistoryOrganizer.loadAndShowSearchHistory();
		showTickets();
	}

	private void addListenersToFlexTables() {
		addListenerToFlexTable(resultsFlexTable);
		addListenerToFlexTable(faveFlexTable);
		addListenerToFlexTable(ticketFlexTable);
	}

	private void addListenerToFlexTable(final FlexTable flexTable) {

		flexTable.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int col = flexTable.getCellForEvent(event).getCellIndex();
				if (col == 1) {
					return;
				}
				int row = flexTable.getCellForEvent(event).getRowIndex();
				String parkingID = null;
				if (flexTable == resultsFlexTable)
					parkingID = idList.get(row);
				if (flexTable == faveFlexTable)
					parkingID = faveList.get(row);
				if (flexTable == ticketFlexTable)
					parkingID = ticketList.get(row);

				if (parkingID.equals("noresults")) {
					return;
				} else {
					System.out.println("I have clicked on parking with ID: "
							+ parkingID);
					// get corresponding ParkingLocation with parkingID
					parkService.getParking(parkingID,
							new AsyncCallback<ParkingLocation>() {

						@Override
						public void onFailure(Throwable caught) {

						}

						@Override
						public void onSuccess(
								final ParkingLocation parking) {
							if (zoom == false) {
								zoom = true;
								theMap.setZoom(17);
							}

							Button addFaveButton = new Button(
									"Add to Faves");
							addFaveHandler(addFaveButton, parking);

							Button addTicket = new Button(
									"Add Parking Ticket");
							addTicketHandler(addTicket, parking);

							parking.displayPopup(theMap, infoWindow,
									addFaveButton, addTicket);

						}

					});

				}
			}
		});

	}

	private void addListenerToStatsResults() {
		faveStatsFT.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				int row = faveStatsFT.getCellForEvent(event).getRowIndex() - 1;
				if (row == -1) {
					return;
				}

				String parkingID = faveStatsList.get(row);

				if (parkingID.equals("header")) {
					return;
				} else {
					System.out.println("I have clicked on parking with ID: "
							+ parkingID);
					// get corresponding ParkingLocation with parkingID
					parkService.getParking(parkingID,
							new AsyncCallback<ParkingLocation>() {

						@Override
						public void onFailure(Throwable caught) {
						}

						@Override
						public void onSuccess(
								final ParkingLocation parking) {
							if (zoom == false) {
								zoom = true;
								theMap.setZoom(17);
							}
							parking.displayPopup(theMap, infoWindow);
						}

					});

				}
			}
		});

		ticketStatsFT.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				int col = ticketStatsFT.getCellForEvent(event).getCellIndex();
				int row = ticketStatsFT.getCellForEvent(event).getRowIndex() - 1;
				if (col == 1 && row == -1) {
					getMostTicketed("count");
					return;
				} else if (col == 2 && row == -1) {
					getMostTicketed("fine");
					return;
				} else if (row == -1) {
					return;
				}

				String parkingID = ticketStatsList.get(row);

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
						parking.displayPopup(theMap, infoWindow);
					}

				});

			}
		});
	}

	private void addListenerToMarker() {
		// Make the popup appear when you click on the map marker
		Marker.ClickHandler markerClickHandler = new Marker.ClickHandler() {
			@Override
			public void handle(MouseEvent event) {
				System.out.println("Marker has been clicked");
				infoWindow.setContent(createSearchLocationPopup());
				infoWindow.setPosition(mapOperator.marker.getPosition());
				infoWindow.open(theMap);
			}
		};
		mapOperator.marker.addClickListener(markerClickHandler);
	}

	private void addListenerToSortBox() {
		// Listen for events on the sortBox
		sortBox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {

				System.out.println("Changed sorting");
				displayParkings(filteredParkings);

			}
		});

	}

	private void addListenersToButtons() {

		// Listen for key press on search box

		searchBox.getTextBox().addKeyPressHandler(new KeyPressHandler() {

			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					mapOperator.clearMarker();
					String address = searchBox.getText();
					if (address.equals("")) {
						infoWindow.close();
						displayDir.setMap(null);
						displayDir.setPanel(null);
						filterParkings();
					} else {
						System.out.println("About to call searchLoc");
						searchLoc(address);
					}
					tabs.selectTab(0);
				}
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
				searchBox.getTextBox().setText("");
				infoWindow.close();
				mapOperator.clearCircle();
				mapOperator.clearMap();
				mapOperator.clearMarker();
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
				mapOperator.clearMarker();
				String address = searchBox.getText();
				if (address.equals("")) {
					infoWindow.close();
					displayDir.setMap(null);
					displayDir.setPanel(null);
					filterParkings();
				} else {
					searchLoc(address);
				}
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

		clearHistoryButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				searchHistoryOrganizer.clearHistory();
			}
		});

		setColor.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				loadDataService
				.getParking(new AsyncCallback<ParkingLocation[]>() {

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

							loadDataService.setColor(color,
									p.getParkingID(),
									new AsyncCallback<Void>() {
								@Override
								public void onFailure(
										Throwable caught) {
								}

								@Override
								public void onSuccess(
										Void result) {
								}
							});
						}
					}
				});
			}
		});

	}

	private void calculateAverage() {

		if (totalNum == 0) {
			avgPrice.setText("Please click on Download Data above first.");
		} else {
			String address = avgPriceAddress.getText();

			if (address.equals("") || avgPriceRadius.getText().equals("")) {
				avgPrice.setText("Please enter the values above.");
			}

			else {
				final double radius = Double.parseDouble(avgPriceRadius
						.getText().replaceAll("[^\\d.]", ""));
				// removes non numerical characters

				GeocoderRequest request = GeocoderRequest.create();
				request.setAddress(address + " Vancouver");
				request.setRegion("ca");
				request.setLocation(theMap.getCenter());
				geocoder.geocode(request, new Geocoder.Callback() {

					@Override
					public void handle(JsArray<GeocoderResult> results,
							GeocoderStatus status) {
						if (status == GeocoderStatus.OK) {
							searchResult = results;
							LatLng latlong = searchResult.get(0).getGeometry()
									.getLocation();
							String addr = searchResult.get(0)
									.getFormattedAddress();
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
							} else {
								double avg = totalPrice / i;
								NumberFormat fmt = NumberFormat
										.getFormat("0.00");
								String avgP = fmt.format(avg);
								avgPrice.setText("Average Price within "
										+ Double.toString(radius) + "m of "
										+ addr + ": $" + avgP + "/hr");
							}

						}
					}
				});
			}
		}

	}

	private void calculateAvgCriteria() {
		userInfoService.getAvgCriteria(new AsyncCallback<Criteria>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Criteria result) {
				// TODO Auto-generated method stub
				System.out.println("At Get Avg Crit");


				NumberFormat fmt = NumberFormat
						.getFormat("###0.##");


				Double radius = result.getRadius();
				Double price = result.getMaxPrice();
				Double time = result.getMinTime();
				NumberFormat formatter = NumberFormat.getCurrencyFormat("CAD");
				String formattedPrice = formatter.format(price);
				String radiusformat = fmt.format(radius);
				String timeformat = fmt.format(time);

				Label priceLabel = new Label("Average Max Price: "
						+ formattedPrice + "/hr");

				//	Double time = (double) (Math.round(result.getMinTime() * 100) / 100);
				Label timeLabel = new Label("Average Min Time: " + timeformat + "hrs");
				Label radiusLabel = new Label("Average Radius: " + radiusformat + "m");

				avgCritVP.add(radiusLabel);
				avgCritVP.add(priceLabel);
				avgCritVP.add(timeLabel);
			}
		});
	}

	// Returns true if the endpoints or midpoint of the parking location are
	// within radius metres of point
	private boolean isInRadius(ParkingLocation p, Double radius, double ctrLat,
			double ctrLng) {
		double startlat = p.getStartLat();
		double startlong = p.getStartLong();
		double endlat = p.getEndLat();
		double endlong = p.getEndLong();
		return (distance(startlat, startlong, ctrLat, ctrLng) <= radius
				|| distance(endlat, endlong, ctrLat, ctrLng) <= radius || distance(
						(startlat + endlat) / 2, (startlong + endlong) / 2, ctrLat,
						ctrLng) <= radius);
	}

	// Returns the distance between two points in metres, given their lats and
	// longs
	private double distance(double lat1, double lon1, double lat2, double lon2) {
		double theta = lon1 - lon2;
		double dist = Math.sin(Math.toRadians(lat1))
				* Math.sin(Math.toRadians(lat2))
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2))
				* Math.cos(Math.toRadians(theta));
		dist = Math.acos(dist);
		dist = Math.toDegrees(dist);
		dist = dist * 60 * 1151.5;
		dist = dist * 1.609344;
		return dist;
	}

	private void addListenersToSliders() {

		// Update max price value label when slider moves
		priceFilterSlider
		.addBarValueChangedHandler(new BarValueChangedHandler() {
			public void onBarValueChanged(BarValueChangedEvent event) {

				double maxPrice = ((double) event.getValue()) / 2;
				// Divide by two to get non-integer prices
				String formatted = NumberFormat.getFormat("#0.00")
						.format(maxPrice);
				maxPriceValueLabel.setText("$" + formatted + " / hr");
				filterParkings();
				saveCriteria();
			}
		});

		// Update min time value label when slider moves
		timeFilterSlider
		.addBarValueChangedHandler(new BarValueChangedHandler() {
			public void onBarValueChanged(BarValueChangedEvent event) {
				minTimeValueLabel.setText(event.getValue() + " hrs");
				filterParkings();
				saveCriteria();
			}
		});

		// Update max radius value label when slider moves
		radiusFilterSlider
		.addBarValueChangedHandler(new BarValueChangedHandler() {
			public void onBarValueChanged(BarValueChangedEvent event) {
				maxRadiusValueLabel.setText(event.getValue() + " m");
				filterParkings();
				saveCriteria();
			}
		});
	}

	private void createMap() {
		System.out.println("Map created");
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

	private void initializeDriverLayout() {
		VerticalPanel signoutPanel = new VerticalPanel();
		signOutLink.setHref(loginInfo.getLogoutUrl());
		mainStatsVP.clear();
		RootPanel.get("parkMe").add(mainPanel);
		mainPanel.setSpacing(10);
		System.out.println("added main panel to root panel");


		signoutPanel.setSize("100px", "30px");
		signoutPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		signoutPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		signoutPanel.add(signOutLink);

		// Set up filterPanel
		filterPanel.setSize("450px", "100px");
		filterPanel.addStyleName("filterPanel");
		filterPanel.add(maxPriceLabel, 1, 10);
		filterPanel.add(minTimeLabel, 1, 40);
		filterPanel.add(maxRadiusLabel, 1, 70);
		filterPanel.add(priceFilterSlider, 130, 5);
		filterPanel.add(timeFilterSlider, 130, 35);
		filterPanel.add(radiusFilterSlider, 130, 65);
		filterPanel.add(maxPriceValueLabel, 350, 10);
		filterPanel.add(minTimeValueLabel, 350, 40);
		filterPanel.add(maxRadiusValueLabel, 350, 70);

		// Set up sortBox
		sortBox.addItem("Price");
		sortBox.addItem("Time Limit");
		sortBox.addItem("Distance");
		sortBox.setVisibleItemCount(1);
		sortPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		sortPanel.add(sortLabel);
		sortPanel.add(sortBox);

		// Set up searchPanel
		searchPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		searchPanel.add(searchBox);
		searchPanel.add(searchButton);
		searchPanel.add(clearDataButton);
		searchPanel.add(signoutPanel);
		searchLabel
		.setText("Enter Address (or leave blank to search whole Vancouver):");

		// ADMIN CONTROLS:
		// tabPanel.add(loadDataButton);
		// tabPanel.add(getAddressesButton);
		// tabPanel.add(setColor);
		// tabPanel.add(displayDataButton);
		// tabPanel.add(clearDataButton);
		// tabPanel.add(filterButton);
		// tabPanel.add(downloadData);
		// tabPanel.add(signOutLink);

		resultsFlexTable.setCellPadding(5);
		faveFlexTable.setCellPadding(5);
		histFlexTable.setCellPadding(5);
		ticketFlexTable.setCellPadding(5);

		resultsScroll.add(resultsFlexTable);
		resultsScroll.setStyleName("scroll");
		faveScroll.add(faveFlexTable);

		VerticalPanel histPanel = new VerticalPanel();
		histScroll.add(histPanel);
		histPanel.add(clearHistoryButton);
		histPanel.add(histFlexTable);
		VerticalPanel rtabPanel = new VerticalPanel();
		rtabPanel.add(sortPanel);
		sortPanel.setHeight("30px");
		rtabPanel.add(resultsScroll);

		// TICKETS
		VerticalPanel ticketPanel = new VerticalPanel();
		ticketScroll.add(ticketPanel);
		ticketPanel.add(new Label("Parking Ticket History"));
		ticketPanel.add(ticketFlexTable);

		// mainHorzPanel.add(resultsScroll);

		flowpanel = new FlowPanel();
		flowpanel.add(rtabPanel);
		tabs.add(flowpanel, "Results");

		flowpanel = new FlowPanel();
		flowpanel.add(faveScroll);
		tabs.add(flowpanel, "Favorites");

		flowpanel = new FlowPanel();
		flowpanel.add(histScroll);
		tabs.add(flowpanel, "History");

		flowpanel = new FlowPanel();
		flowpanel.add(ticketScroll);
		tabs.add(flowpanel, "Tickets");

		// set up statistics tab

		avgPriceVP.add(avgPriceLabel);
		avgPriceVP.add(avgPriceAddress);
		avgPriceVP.add(avgPriceRadiusLbl);
		avgPriceVP.add(avgPriceRadius);
		avgPriceVP.add(avgPriceButton);
		avgPriceVP.add(avgPrice);
		mainStatsVP.add(avgPriceVP);
		mainStatsVP.add(avgCritVP);

		// calculateAvgCriteria();
		statsScroll.add(mainStatsVP);
		flowpanel = new FlowPanel();
		flowpanel.add(statsScroll);
		tabs.add(flowpanel, "Statistics");
		// TODO: move out of here

		flowpanel = new FlowPanel();
		flowpanel.add(dirScroll);
		tabs.add(flowpanel, "Directions");

		tabs.selectTab(0);

		// Put together main panels

		leftVertPanel.add(searchLabel);
		leftVertPanel.add(searchPanel);
		leftVertPanel.add(filterPanel);
		leftVertPanel.add(tabs);
		mainPanel.add(leftVertPanel);
		mainPanel.add(rightVertPanel);
		mainPanel.setSpacing(10);

		// Set sizes for elements

		String scrollHeight = Window.getClientHeight() - 295 + 5 + "px";
		String scrollWidth = 0.4 * Window.getClientWidth() - 60 + "px";

		// resultsScroll.setSize("100%", "100%");
		resultsScroll.setSize(scrollWidth, Window.getClientHeight() - 335 + 15
				+ "px");
		rtabPanel.setSize(scrollWidth, scrollHeight);
		faveScroll.setSize(scrollWidth, scrollHeight);
		histScroll.setSize(scrollWidth, scrollHeight);
		statsScroll.setSize(scrollWidth, scrollHeight);
		ticketScroll.setSize(scrollWidth, scrollHeight);
		dirScroll.setSize(scrollWidth, scrollHeight);
		tabs.setSize(0.4 * Window.getClientWidth() - 50 + "px", "100%");

		resultsFlexTable.setSize(scrollWidth, "100%");
		faveFlexTable.setSize(scrollWidth, "100%");
		histFlexTable.setSize(scrollWidth, "100%");
		ticketFlexTable.setSize(scrollWidth, "100%");
		leftVertPanel.setSize(0.3 * Window.getClientWidth() + "px",
				Window.getClientHeight() - 35 + "px");
		rightVertPanel.setSize(0.7 * Window.getClientWidth() - 120 + "px",
				Window.getClientHeight() - 50 + "px");

		mapPanel.setSize("100%", "100%");
		// mainPanel.setSpacing(0);
		// mainPanel.setSize("100%", "100%");

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
		displayParkings(allParkings);
	}

	private void displayParkings(List<ParkingLocation> parkingLocations) {
		mapOperator.clearMap();
		resultsFlexTable.removeAllRows();
		idList.clear();

		if (parkingLocations.size() == 0) {
			resultsFlexTable.setText(0, 0, "No results found.");
			idList.add("noresults");
		} else {

			int index = sortBox.getSelectedIndex();
			parkingLocations = sortBy(sortBox.getItemText(index),
					parkingLocations);

			for (ParkingLocation p : parkingLocations) {
				displayParking(p);
			}
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
		addFaveHandler(addFaveButton, parkingLoc);

		Button addTicket = new Button("Add Parking Ticket");
		addTicketHandler(addTicket, parkingLoc);

		mapOperator.drawOnMap(parkingLoc, infoWindow, addFaveButton, addTicket);
		idList.add(parkingLoc.getParkingID());
		System.out.println("Currently printing parking "
				+ parkingLoc.getParkingID());
	}

	private void filterParkings() {
		filteredParkings.clear();
		LatLng searchPoint;

		double maxPrice = ((double) priceFilterSlider.getValue() / 2);
		System.out.println(maxPrice);
		double minTime = (double) timeFilterSlider.getValue();
		double maxRadius;

		if (searchBox.getText().equals("")) {
			System.out.println("Centering it to downtown");
			searchPoint = LatLng.create(49.2814, -123.12);
			maxRadius = 99999999;
			mapOperator.clearCircle();

		}

		else {
			searchPoint = searchResult.get(0).getGeometry().getLocation();
			System.out.println("Filtering for results around "
					+ searchResult.get(0).getFormattedAddress());
			maxRadius = (double) radiusFilterSlider.getValue();
			if (maxRadius == 0) {
				maxRadius = 99999999;
			}
			mapOperator.drawCircle(searchPoint, maxRadius);

		}

		System.out
		.println("Filtering with maxPrice = " + maxPrice
				+ " and minTime = " + minTime + " and maxRadius = "
				+ maxRadius);

		List<ParkingLocation> filtered = new ArrayList<ParkingLocation>();
		for (int i = 0; i < totalNum; i++) {
			ParkingLocation p = allParkings.get(i);
			if ((p.getPrice() <= maxPrice)
					&& (p.getLimit() >= minTime)
					&& isInRadius(p, maxRadius, searchPoint.lat(),
							searchPoint.lng())) {
				filtered.add(p);
			}
			// System.out.println("Found " + filtered.size() + " locations");
		}

		filteredParkings = filtered;

		displayParkings(filtered);
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
		displayDir.setMap(null);
		displayDir.setPanel(null);
		System.out.println("In searchLoc");

		GeocoderRequest request = GeocoderRequest.create();
		request.setAddress(address + " Vancouver");
		request.setRegion("ca");

		geocoder.geocode(request, new Geocoder.Callback() {

			@Override
			public void handle(JsArray<GeocoderResult> results,
					GeocoderStatus status) {
				System.out.println("Handling geocoder results");
				if (status == GeocoderStatus.OK) {
					System.out.println("results are okay");
					searchHistoryOrganizer.addAndSaveSearch(address);
					searchResult = results;
					final LatLng latlong = searchResult.get(0).getGeometry()
							.getLocation();

					System.out.println("About to call setMarker");
					mapOperator.setMarker(latlong);
					theMap.setCenter(latlong);
					theMap.setZoom(17);
					System.out.println("About to filter parking locations");
					filterParkings();
				}
			}
		});

	}

	private void downloadData() {
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
				loadingPage.hide();
				filterParkings();

				initializeFlexTables();
			}
		});
		loadingPage.show();

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
		addFaveHandler(addFaveButton, parkingLoc);

		Button addTicket = new Button("Add Parking Ticket");
		addTicketHandler(addTicket, parkingLoc);// TODO

		mapOperator.drawOnMap(parkingLoc, infoWindow, addFaveButton, addTicket);
		faveList.add(parkingLoc.getParkingID());
		System.out.println("Currently printing parking "
				+ parkingLoc.getParkingID());
	}

	private void displayTickets(TicketInfo[] ticketInfo) {
		// mapOperator.clearMap();
		String[] ids = new String[ticketInfo.length];
		for (int i = 0; i < ticketInfo.length; i++) {
			ids[i] = ticketInfo[i].getParkingID();
		}

		ParkingLocation[] plocs = new ParkingLocation[ticketInfo.length];
		System.out.println("ploc length " + plocs.length);

		plocs = getParkingLocationsByIds(ids);
		System.out.println("ploc length " + plocs.length);

		for (int i = 0; i < plocs.length; i++) {
			displayTicket(plocs[i], ticketInfo[i]);
		}
	}

	private ParkingLocation[] getParkingLocationsByIds(String[] ids) {
		if (allParkings.size() == 0) {
			Window.alert("Parking Data not downloaded");
			return null;
		}
		ParkingLocation[] plocs = new ParkingLocation[ids.length];
		for (int i = 0; i < ids.length; i++) {
			plocs[i] = findParkingLocationById(ids[i]);
		}
		return plocs;
	}

	private ParkingLocation findParkingLocationById(String id) {

		System.out.println("parking location id is " + id);

		for (ParkingLocation p : allParkings) {
			if (p.getParkingID().equals(id))
				return p;
		}
		return new ParkingLocation(id, 00.00, 00.00, 49.251, -123.119, 49.251,
				-123.119, "No location found", "black");

	}

	private void displayTicket(final ParkingLocation parkingLoc,
			TicketInfo ticketInfo) {

		final VerticalPanel info = new VerticalPanel();
		HTML street = new HTML("<b>" + parkingLoc.getStreet() + "</b>");
		HTML fine = new HTML("<u>Fine:</u> $" + ticketInfo.getFine());
		info.add(street);
		info.add(fine);
		final int row = ticketFlexTable.getRowCount();

		if (parkingLoc.getColor().equals("#66CD00")) {
			ticketFlexTable.getColumnFormatter().setWidth(1, "30 px");
			ticketFlexTable.getCellFormatter().addStyleName(row, 0, "parking1");
		} else if (parkingLoc.getColor().equals("#9BD500")) {
			ticketFlexTable.getColumnFormatter().setWidth(1, "30 px");
			ticketFlexTable.getCellFormatter().addStyleName(row, 0, "parking2");
		} else if (parkingLoc.getColor().equals("#B7D900")) {
			ticketFlexTable.getColumnFormatter().setWidth(1, "30 px");
			ticketFlexTable.getCellFormatter().addStyleName(row, 0, "parking3");
		} else if (parkingLoc.getColor().equals("#E0CF00")) {
			ticketFlexTable.getColumnFormatter().setWidth(1, "30 px");
			ticketFlexTable.getCellFormatter().addStyleName(row, 0, "parking4");
		} else if (parkingLoc.getColor().equals("#E8A100")) {
			ticketFlexTable.getColumnFormatter().setWidth(1, "30 px");
			ticketFlexTable.getCellFormatter().addStyleName(row, 0, "parking5");
		} else if (parkingLoc.getColor().equals("#EC8800")) {
			ticketFlexTable.getColumnFormatter().setWidth(1, "30 px");
			ticketFlexTable.getCellFormatter().addStyleName(row, 0, "parking6");
		} else if (parkingLoc.getColor().equals("#F35400")) {
			ticketFlexTable.getColumnFormatter().setWidth(1, "30 px");
			ticketFlexTable.getCellFormatter().addStyleName(row, 0, "parking7");
		} else if (parkingLoc.getColor().equals("#FB1D00")) {
			ticketFlexTable.getColumnFormatter().setWidth(1, "30 px");
			ticketFlexTable.getCellFormatter().addStyleName(row, 0, "parking8");
		} else if (parkingLoc.getColor().equals("#FF0000")) {
			ticketFlexTable.getColumnFormatter().setWidth(1, "30 px");
			ticketFlexTable.getCellFormatter().addStyleName(row, 0, "parking9");
		}
		ticketFlexTable.setWidget(row, 0, info);

		final Button addFaveButton = new Button("Add to Faves");
		addFaveHandler(addFaveButton, parkingLoc);

		Button addTicket = new Button("Add Parking Ticket");
		addTicketHandler(addTicket, parkingLoc);

		mapOperator.drawOnMap(parkingLoc, infoWindow, addFaveButton, addTicket);
		ticketList.add(parkingLoc.getParkingID());
		System.out.println("Currently printing parking "
				+ parkingLoc.getParkingID());
	}

	private void addTicketHandler(Button addTicket,
			final ParkingLocation parkingLoc) {

		addTicket.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				VerticalPanel ticketPan = new VerticalPanel();

				Label ticketLabel = new Label("Enter Fine: ");
				fine.setText("0.00");
				Button addFine = new Button("Submit Fine");

				ticketPan.add(ticketLabel);
				ticketPan.add(fine);
				ticketPan.add(addFine);
				infoWindow.setContent(ticketPan);
				infoWindow.open(theMap);


				addFine.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						addFineToServer(parkingLoc);
					}

				});

				fine.addKeyPressHandler(new KeyPressHandler() {
					public void onKeyPress(KeyPressEvent event) {
						//event.stopPropagation();
						if (event.getCharCode() == KeyCodes.KEY_ENTER) {
							//event.stopPropagation();
							addFineToServer(parkingLoc);
							
						}
						
					}
				});
			}
		});

	}

	private void addFaveHandler(Button addFaveButton,
			final ParkingLocation parkingLoc) {
		addFaveButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {

				FaveAsync fave = GWT.create(Fave.class);
				fave.addFave(parkingLoc.getParkingID(),
						new AsyncCallback<Void>() {

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
			faveFlexTable.setText(0, 0,
					"You haven't added anything to favorites yet.");
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
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(String[] result) {
				faveFlexTable.removeAllRows();
				faveList.clear();
				if (result.length == 0) {
					faveFlexTable.setText(0, 0,
							"You haven't added anything to favorites yet.");

				}

				else {
					parkService.getParkings(result,
							new AsyncCallback<ParkingLocation[]>() {

								@Override
								public void onFailure(Throwable caught) {
								}

								@Override
								public void onSuccess(ParkingLocation[] result) {
									displayFavorites(result);
								}
							});
				}
			}
		});
	}

	public void showTickets() {
		ticketService.getTickets(new AsyncCallback<TicketInfo[]>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(TicketInfo[] result) {
				ticketFlexTable.removeAllRows();
				ticketList.clear();
				if (result.length == 0) {
					faveFlexTable.setText(0, 0,
							"You haven't reported any parking tickets yet.");
				}

				else {
					displayTickets(result);
				}
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
	private List<ParkingLocation> sortBy(String sortMode,
			List<ParkingLocation> parkingLocations) {
		System.out.println(sortMode);
		if (searchBox.getText().equals("") && sortMode.equals("Distance")) {
			System.out.println("it was equal to distance");
			return parkingLocations;
		} else {
			Comparator<ParkingLocation> c = getComparator(sortMode);
			Collections.sort(parkingLocations, c);
			return parkingLocations;
		}
	}

	private Comparator<ParkingLocation> getComparator(String sortParam) {
		if ("Price".equals(sortParam)) {
			return new Comparator<ParkingLocation>() {
				@Override
				public int compare(ParkingLocation o1, ParkingLocation o2) {
					return new Double(o1.getPrice()).compareTo(new Double(o2
							.getPrice()));
				}
			};
		} else if ("Time Limit".equals(sortParam)) {
			return new Comparator<ParkingLocation>() {
				@Override
				public int compare(ParkingLocation o1, ParkingLocation o2) {
					return new Double(o1.getLimit()).compareTo(new Double(o2
							.getLimit()));
				}
			};
		} else if ("Distance".equals(sortParam)) {
			return new Comparator<ParkingLocation>() {
				@Override
				public int compare(ParkingLocation o1, ParkingLocation o2) {
					LatLng point = searchResult.get(0).getGeometry()
							.getLocation();
					double pointx = point.lat();
					double pointy = point.lng();
					double distanceStart1 = distance(pointx, pointy,
							o1.getStartLat(), o1.getStartLong());
					double distanceEnd1 = distance(pointx, pointy,
							o1.getEndLat(), o1.getEndLong());
					double distanceStart2 = distance(pointx, pointy,
							o2.getStartLat(), o2.getStartLong());
					double distanceEnd2 = distance(pointx, pointy,
							o2.getEndLat(), o2.getEndLong());

					double distance1 = distanceStart1;
					double distance2 = distanceStart2;

					if (distanceEnd1 < distanceStart1) {
						distance1 = distanceEnd1;
					}
					if (distanceEnd2 < distanceStart2) {
						distance2 = distanceEnd2;
					}
					return new Double(distance1)
					.compareTo(new Double(distance2));
				}
			};
		} else {
			throw new IllegalArgumentException("invalid sort field '"
					+ sortParam + "'");
		}
	}

	private void renderApp(String token, String type) {

		System.out.println(token);
		token = token.replace("#", "");

		if (token == null || "".equals(token) || "#".equals(token)) {
			token = "home";
		}

		if (token.endsWith("home")) {
			renderFB(type);

		} else {
			Window.alert("Unknown  url " + token);
		}
	}

	public void onValueChange(ValueChangeEvent<String> event) {
		renderApp(event.getValue(), usertype);
	}

	private void createFBEvent(final String addr) {
		// TODO: popup asking for event name & start time

		final String title = eventName.getText();
		String date = eventTime.getText();
		if (!date.matches("^\\d{4}-(1[1-2]|0\\d)-[1-3]\\d")) {
			Window.alert("Date is not of the correct form");
		} else {
			System.out.println("Creating FB Event");
			JSONObject param = new JSONObject();
			param.put("name", new JSONString(title));
			param.put("start_time", new JSONString(date));
			param.put("location", new JSONString(addr));
			param.put(
					"description",
					new JSONString(
							"This event was automatically generated by the ParkMe app."));
			fbCore.api("/me/events", "post", param.getJavaScriptObject(),
					new AsyncCallback<JavaScriptObject>() {
				@Override
				public void onFailure(Throwable caught) {
				}

				@Override
				public void onSuccess(JavaScriptObject result) {
					JSONObject res = new JSONObject(result);
					String id = res.get("id").toString();

					Label eventTitle = new Label(
							"Successfully created Facebook event "
									+ title);
					String url = "<a href=\"http://www.facebook.com/events/"
							+ id.substring(1, id.length() - 1)
							+ "\" target=\"_blank\">Event Link</a>";
					HTML link = new HTML(url);
					System.out.println(url);
					VerticalPanel fbE = new VerticalPanel();
					fbE.add(eventTitle);
					fbE.add(link);
					infoWindow.setContent(fbE);
					infoWindow.open(theMap);
				}
			});

		}
	}

	private void getDirections(final LatLng latlong) {

		displayDir.setMap(null);
		displayDir.setPanel(null);
		dr.setDestination(latlong);

		if (Geolocation.isSupported()) {
			// get Geo Location
			Geolocation geo = Geolocation.getGeolocation();
			geo.getCurrentPosition(new PositionCallback() {
				public void onFailure(PositionError error) {
					// Handle failure
				}

				public void onSuccess(Position position) {
					Coordinates coords = position.getCoords();
					double latitude = coords.getLatitude();
					double longitude = coords.getLongitude();
					displayDirections(LatLng.create(latitude, longitude));
				}
			});
		}

		else {
			Window.alert("Geolocation is not supported. Will set origin to UBC.");
			displayDirections(LatLng.create(49.2661156, -123.2457198));
		}

	}

	private void displayDirections(LatLng latlong) {

		dr.setOrigin(latlong);
		dr.setTravelMode(TravelMode.DRIVING);
		ds.route(dr, new DirectionsService.Callback() {

			@Override
			public void handle(DirectionsResult result, DirectionsStatus status) {

				if (status.equals(DirectionsStatus.OK)) {
					displayDir.setMap(theMap);
					displayDir.setPanel(dirScroll.getElement());
					tabs.selectTab(5);
					displayDir.setDirections(result);
				}
			}
		});
	}

	private void loadDriver() {
		// stub
		RootPanel.get("parkMe").clear();
		loadParkMe();
	}

	private void loadAdmin() {
		// stub
		RootPanel.get("parkMe").clear();
		addListenersToButtons();
		addListenerToStatsResults();
		initAdminLayout();
		createMap();
	}

	private void initAdminLayout() {
		// buttons to load data & street info
		// no option to filter
		// whole page about statistics
		SimplePanel dummyPanel = new SimplePanel();
		dummyPanel.setSize("200px", "20px");
		VerticalPanel signoutPanel = new VerticalPanel();
		ScrollPanel favescroll = new ScrollPanel();
		VerticalPanel vp1 = new VerticalPanel();
		VerticalPanel vp2 = new VerticalPanel();
		Button viewAsDriver = new Button("View As Driver");
		signOutLink.setHref(loginInfo.getLogoutUrl());
		signoutPanel.setSize("200px", "50px");
		signoutPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		signoutPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		signoutPanel.add(signOutLink);
		RootPanel.get("parkMe").add(mainPanel);

		mainPanel.add(vp1);
		mainPanel.add(vp2);
		mainPanel.add(rightVertPanel);

		mapPanel.setSize("100%", "100%");

		rightVertPanel.setSize(Window.getClientWidth() - 470 + "px",
				Window.getClientHeight() - 50 + "px");


		vp1.setSize("200px", "100%");
		vp2.setSize("230px", "100%");
		vp1.add(new HTML("<center><b>ParkMe<br>Administrator</b></center>"));
		signOutLink.setSize("200px", "3em");
		signOutLink.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);


		loadDataButton.setSize("200px", "3em");
		getAddressesButton.setSize("200px", "3em");
		viewAsDriver.setSize("200px", "3em");
		vp1.add(dummyPanel);
		vp1.add(loadDataButton);
		vp1.add(getAddressesButton);
		mainPanel.setSpacing(10);
		vp1.add(viewAsDriver);
		vp1.add(signoutPanel);
		avgCritVP.add(new HTML("<b>Average User Criterias:</b>"));
		mainStatsVP.add(avgCritVP);
		calculateAvgCriteria();

		favescroll.add(faveStatsFT);
		ticketscroll.add(ticketStatsFT);
		mainStatsVP.add(new HTML("<br><br><b>Most Favorited Locations:</b>"));
		mainStatsVP.add(favescroll);
		mainStatsVP.add(new HTML("<br><br><b>Most Ticketed Locations:</b>"));
		mainStatsVP.add(ticketscroll);
		faveStatsFT.setCellPadding(5);
		ticketStatsFT.setCellPadding(5);
		favescroll.setHeight("160px");
		ticketscroll.setHeight("160px");

		getMostFaved();
		getMostTicketed("fine");
		userInfoService.getNumUsers(new AsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(Long result) {
				mainStatsVP
				.add(new HTML(
						"<br><br><b>Number of registered users:</b> "
								+ result));

			}
		});
		fave.getNumFaves(new AsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(Long result) {
				mainStatsVP.add(new HTML("<b>Number of total favorites:</b> "
						+ result));

			}
		});
		ticketService.getNumTickets(new AsyncCallback<Long>() {

			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Long result) {
				mainStatsVP.add(new HTML("<b>Number of tickets uploaded:</b> "
						+ result));
			}

		});

		vp2.add(mainStatsVP);

		viewAsDriver.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				RootPanel.get("parkMe").clear();
				mainPanel.clear();

				addListenersToFlexTables();
				addListenerToSortBox();
				initializeSliderValues();
				initializeLoadingPage();
				initializeDriverLayout();
				downloadData();
				addListenersToSliders();
				addListenerToMarker();

				System.out.println("finished click handler");
			}
		});


	}

	private void getMostFaved() {
		System.out.println("Getting most faved");
		faveStatsFT.setWidget(0, 0, new HTML("<b>Parking ID</b>"));
		faveStatsFT.setWidget(0, 1, new HTML("<b>Count</b>"));
		// faveStatsList.add("header");
		final Comparator<ParkingStats> comp = new Comparator<ParkingStats>() {

			@Override
			public int compare(ParkingStats o1, ParkingStats o2) {
				// TODO Auto-generated method stub
				return o2.getCount().compareTo(o1.getCount());
			}

		};
		fave.getMostFaved(new AsyncCallback<ParkingStats[]>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onSuccess(ParkingStats[] result) {
				List<ParkingStats> fs = new ArrayList<ParkingStats>();

				for (ParkingStats f : result) {
					// String count = ""+f.getCount()+"";
					fs.add(f);
				}
				Collections.sort(fs, comp);

				for (int i = 0; i < fs.size(); i++) {
					int row = faveStatsFT.getRowCount();
					faveStatsFT.setText(row, 0, fs.get(i).getParkingID());
					faveStatsFT
					.setText(row, 1, fs.get(i).getCount().toString());
					faveStatsList.add(fs.get(i).getParkingID());
				}
				String[] faveStatsArray = faveStatsList
						.toArray(new String[faveStatsList.size()]);

				parkService.getParkings(faveStatsArray,
						new AsyncCallback<ParkingLocation[]>() {

							@Override
							public void onFailure(Throwable caught) {
							}

							@Override
							public void onSuccess(ParkingLocation[] result) {
								mapOperator.drawLocs(result, infoWindow);
							}

						});
			}
		});

	}

	private void getMostTicketed(final String comp) {
		ticketStatsFT.removeAllRows();
		ticketStatsList.clear();
		System.out.println("Getting most ticketed");
		// ticketStatsList.add("header");

		ticketStatsFT.setWidget(0, 0, new HTML("<b>Parking ID</b>"));
		ticketStatsFT.setWidget(0, 1, new HTML("<b>Count</b>"));
		ticketStatsFT.setWidget(0, 2, new HTML("<b>Total Fine</b>"));

		@SuppressWarnings("unused")
		final Comparator<ParkingStats> byCount = new Comparator<ParkingStats>() {

			@Override
			public int compare(ParkingStats o1, ParkingStats o2) {
				// TODO Auto-generated method stub
				return o2.getCount().compareTo(o1.getCount());
			}

		};

		final Comparator<ParkingStats> byFine = new Comparator<ParkingStats>() {

			@Override
			public int compare(ParkingStats o1, ParkingStats o2) {
				// TODO Auto-generated method stub
				return o2.getFine().compareTo(o1.getFine());
			}

		};

		ticketService.getMostTicketed(new AsyncCallback<ParkingStats[]>() {

			@Override
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(ParkingStats[] result) {
				List<ParkingStats> fs = new ArrayList<ParkingStats>();

				for (ParkingStats f : result) {
					fs.add(f);
				}
				if (comp.equals("fine")) {
					Collections.sort(fs, byFine);
				} else if (comp.equals("count")) {
					Collections.sort(fs, byCount);
				}
				for (int i = 0; i < fs.size(); i++) {

					int row = ticketStatsFT.getRowCount();
					ticketStatsFT.setText(row, 0, fs.get(i).getParkingID());
					ticketStatsFT.setText(row, 1, fs.get(i).getCount()
							.toString());
					ticketStatsFT.setText(row, 2, fs.get(i).getFine()
							.toString());
					ticketStatsList.add(fs.get(i).getParkingID());

				}
				String[] ticketStatsArray = ticketStatsList
						.toArray(new String[ticketStatsList.size()]);

				parkService.getParkings(ticketStatsArray,
						new AsyncCallback<ParkingLocation[]>() {

							@Override
							public void onFailure(Throwable caught) {
							}

							@Override
							public void onSuccess(ParkingLocation[] result) {
								for (ParkingLocation parkingLocation : result) {
									mapOperator.drawOnMap(parkingLocation, infoWindow);
								}

								//mapOperator.drawLocs(result, infoWindow);
							}

						});

			}
		});
	}

	private void initBusinessLayout() {
		signOutLink.setHref(loginInfo.getLogoutUrl());
		RootPanel.get("parkMe").add(mainPanel);
		mainPanel.add(new Label("Business Layout"));
		mainPanel.add(signOutLink);

		// mostly the same as Driver but different viewable statistics

	}

	private void loadCorrectPage(String usertype) {
		initializeSliderValues();
		if (usertype.equals("driver")) {
			loadDriver();
		} else if (usertype.equals("admin")) {
			loadAdmin();
		} else {
			// Window.alert("Can't figure out usertype");
			loadSetUserType();
		}
	}

	private VerticalPanel createSearchLocationPopup() {
		final LatLng latlong = searchResult.get(0).getGeometry().getLocation();
		final String addr = searchResult.get(0).getFormattedAddress();

		Button createEvent = new Button("Create Event");
		Button getDirections = new Button("Directions to Here");
		VerticalPanel main = new VerticalPanel();
		HorizontalPanel buttons = new HorizontalPanel();
		main.add(new Label(addr));
		buttons.add(createEvent);
		buttons.add(getDirections);
		main.add(buttons);

		createEvent.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// infoWindow.open(theMap);
				// mainPan.clear();
				buttonPanel.add(eventCreate);
				// buttonPanel.add(eventCancel);
				eventName.setText("Event Name");
				eventTime.setText("YYYY-MM-DD");
				mainPan.add(eventName);
				mainPan.add(eventTime);
				mainPan.add(buttonPanel);
				infoWindow.setContent(mainPan);
				infoWindow.open(theMap);

				eventCreate.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						createFBEvent(addr);

					}
				});
				eventName.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						// TODO Auto-generated method stub
						eventName.setText("");
					}
				});
				eventTime.addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent event) {
						// TODO Auto-generated method stub
						eventTime.setText("");
					}
				});
				eventName.addKeyPressHandler(new KeyPressHandler() {
					public void onKeyPress(KeyPressEvent event) {
						if (event.getCharCode() == KeyCodes.KEY_ENTER) {
							eventTime.setFocus(true);
						}
					}
				});
				eventTime.addKeyPressHandler(new KeyPressHandler() {
					public void onKeyPress(KeyPressEvent event) {
						if (event.getCharCode() == KeyCodes.KEY_ENTER) {
							createFBEvent(addr);

						}
					}
				});

			}
		});

		getDirections.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				getDirections(latlong);
			}
		});

		return main;
	}

	private void addFineToServer(final ParkingLocation parkingLoc) {
		// TODO Auto-generated method stub
		final String fineString = fine.getText();
		if (!fineString.matches("^\\d*(\\.\\d{0,2})?$")) {
			Window.alert("Fine is not of the correct form");
		} else {
			final Double doubleAmount = Double.parseDouble(fineString);
			TicketServiceAsync ticket = GWT.create(TicketService.class);
			ticket.addTicket(parkingLoc.getParkingID(), doubleAmount,
					new AsyncCallback<Void>() {
						@Override
						public void onSuccess(Void result) {
							NumberFormat formatter = NumberFormat
									.getCurrencyFormat("CAD");
							VerticalPanel success = new VerticalPanel();
							String formattedFine = formatter
									.format(doubleAmount);
							success.add(new Label("Successfully uploaded "
									+ formattedFine + " fine on to server."));
							infoWindow.setContent(success);
							infoWindow.open(theMap);
							displayTicket(parkingLoc,
									new TicketInfo(parkingLoc.getParkingID(),
											doubleAmount));
						}

						@Override
						public void onFailure(Throwable caught) {
						}
					});

		}
	}
}
