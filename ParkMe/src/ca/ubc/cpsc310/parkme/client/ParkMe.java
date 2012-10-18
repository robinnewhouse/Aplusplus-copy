package ca.ubc.cpsc310.parkme.client;

import java.util.List;

import ca.ubc.cpsc310.parkme.client.ParkingLocation;
import ca.ubc.cpsc310.parkme.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.maps.gwt.client.GoogleMap;
import com.google.maps.gwt.client.LatLng;
import com.google.maps.gwt.client.MapOptions;
import com.google.maps.gwt.client.MapTypeId;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ParkMe implements EntryPoint {

	// FILTER UI STUFF
	
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

	private HorizontalPanel tabPanel = new HorizontalPanel();
	
	HorizontalPanel mainHorzPanel = new HorizontalPanel();
	VerticalPanel leftVertPanel = new VerticalPanel();
	Button favoritesButton = new Button("Favorites");
	Button historyButton = new Button("History");
	FlexTable resultsFlexTable = new FlexTable();
	VerticalPanel rightVertPanel = new VerticalPanel();
	HorizontalPanel TitleHorzPanel = new HorizontalPanel();
	Label titleLabel = new Label("Park Me");
	Button loginButton = new Button("Login");			
	VerticalPanel SearchPanel = new VerticalPanel();  //TODO - Figure out how to implement this properly!
	VerticalPanel mapPanel = new VerticalPanel();  //TODO - Frances implement this properly - just reserving space now!

	private final LoadDataServiceAsync loadDataService = GWT.create(LoadDataService.class);
	private final FilterServiceAsync filterService = GWT.create(FilterService.class);
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {


		RootPanel.get("parkMe").add(mainPanel);
		//mainPanel.add(loadDataButton);
		//mainPanel.add(displayDataButton);

		//mainPanel.add(clearDataButton);
	
		//mainPanel.add(timeFilterTextBox);
		//mainPanel.add(filterButton);
		//mainPanel.add(resultsFlexTable);
		
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
		mainPanel.add(tabPanel);
		
		initializeResultsFlexTable();

		//TODO Make first row of Results Table the title
		RootPanel.get("parkMe").add(mainHorzPanel);
		//mainHorzPanel.add(leftVertPanel);
		//leftVertPanel.add(favoritesButton);
		//leftVertPanel.add(historyButton);
		//leftVertPanel.add(loadDataButton);
		//leftVertPanel.add(displayDataButton);
		
		resultsScroll.add(resultsFlexTable);
		mainHorzPanel.add(resultsScroll);
		//leftVertPanel.add(resultsScroll);
		mainHorzPanel.add(rightVertPanel);
		//rightVertPanel.add(TitleHorzPanel);
		//TitleHorzPanel.add(titleLabel);
		//TitleHorzPanel.add(loginButton);
		//rightVertPanel.add(SearchPanel);

		mainPanel.add(mainHorzPanel);
		// Set sizes for elements
		
		resultsScroll.setSize(0.3*Window.getClientWidth()-20 + "px", "100%");
		//mainPanel.setSize("100%", Window.getClientHeight() + "px");
		mainHorzPanel.setSize("100%", Window.getClientHeight()-160 + "px");
		//leftVertPanel.setSize(0.3*Window.getClientWidth()-20 + "px", "100%");
		rightVertPanel.setSize(0.7*Window.getClientWidth()-20 + "px",  "100%");
		mapPanel.setSize("100%", "100%");

		// Give panels borders for debugging purposes
		//mainHorzPanel.setBorderWidth(5);
		//leftVertPanel.setBorderWidth(5);
		//rightVertPanel.setBorderWidth(5);
		mainPanel.setSpacing(10);
	
		mapPanel.setBorderWidth(1);


		// Set up map options
		MapOptions options  = MapOptions.create() ;
		options.setCenter(LatLng.create(49.251, -123.119));   
		options.setZoom(11) ;
		options.setMapTypeId(MapTypeId.ROADMAP);
		options.setDraggable(true);
		options.setMapTypeControl(true);
		options.setScaleControl(true) ;
		options.setScrollwheel(true) ;

		// Add map to mapPanel
		GoogleMap theMap = GoogleMap.create(mapPanel.getElement(), options) ;
		rightVertPanel.add(mapPanel);

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
				System.out.println(resultsFlexTable.getRowCount());
				resultsFlexTable.removeAllRows();
				System.out.println(resultsFlexTable.getRowCount());
				initializeResultsFlexTable();
			}
		});

		// Listen for mouse events on the Parking < 2 Data button.
		filterButton.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				System.out.println("I have clicked on filter button");
				displayFilter();
			}
		});

		// price filter
		priceFilterTextBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				priceFilterTextBox.setText("");
			}
		});

		// time filter
		timeFilterTextBox.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				timeFilterTextBox.setText("");
			}
		});
	}

	private void loadData() {
		loadDataService.loadData(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				// ERROR
				Window.alert("ERROR LOADING DATA");
			}

			@Override
			public void onSuccess(Void result) {
				// TODO Auto-generated method stub
				// SUCCESS Data has been loaded
				Window.alert("DATA LOADED SUCCESSFULLY");
			}

		});
	}

	private void displayData() {
		loadDataService.getParking(new AsyncCallback<ParkingLocation[]>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert("Error getting parking");
			}

			@Override
			public void onSuccess(ParkingLocation[] result) {

				// TODO Auto-generated method stub
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
		int row = resultsFlexTable.getRowCount();
		//		resultsFlexTable.setWidget(row, 0, new Label(parkingLoc.getParkingID()));
		//		resultsFlexTable.setWidget(row, 1, new Label(Double.toString(parkingLoc.getPrice())));
		//		resultsFlexTable.setWidget(row, 2, new Label(Double.toString(parkingLoc.getLimit())));

		resultsFlexTable.setText(row, 0, parkingLoc.getParkingID());
		resultsFlexTable.setText(row, 1, Double.toString(parkingLoc.getPrice()));
		resultsFlexTable.setText(row, 2, Double.toString(parkingLoc.getLimit()));
	}

	private void displayFilter() {

		double maxPrice = Double.parseDouble(priceFilterTextBox.getText()); 
		double minTime = Double.parseDouble(timeFilterTextBox.getText());

		Criteria crit = new Criteria(0,maxPrice,minTime);
		filterService.getParking(crit, new AsyncCallback<ParkingLocation[]>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				Window.alert("Error getting parking");
			}

			@Override
			public void onSuccess(ParkingLocation[] result) {
				// TODO Auto-generated method stub
				Window.alert("Successfully displayed filtered data");


				displayParkings(result);
			}

		});
	}

	private void initializeResultsFlexTable() {
		resultsFlexTable.setText(0, 0, "Parking ID");
		resultsFlexTable.setText(0, 1, "Price");
		resultsFlexTable.setText(0, 2, "Limit");
		resultsFlexTable.getRowFormatter().addStyleName(0, "resultListHeader");
		resultsFlexTable.setCellPadding(6);
	}

}