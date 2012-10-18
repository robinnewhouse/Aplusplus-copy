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

	private Button loadDataButton = new Button("Load Data");
	private Button displayDataButton = new Button("Display Data");
	private VerticalPanel mainPanel = new VerticalPanel();
	
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

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		RootPanel.get("parkMe").add(mainPanel);
		mainPanel.add(loadDataButton);
		mainPanel.add(displayDataButton);
		mainPanel.add(resultsFlexTable);
		resultsFlexTable.setText(0, 0, "Parking ID");
		resultsFlexTable.setText(0, 1, "Price");
		resultsFlexTable.setText(0, 2, "Limit");

		//TODO Make first row of Results Table the title
		RootPanel.get("parkMe").add(mainHorzPanel);
		mainHorzPanel.add(leftVertPanel);
		leftVertPanel.add(favoritesButton);
		leftVertPanel.add(historyButton);
		leftVertPanel.add(loadDataButton);
		leftVertPanel.add(displayDataButton);
		leftVertPanel.add(resultsFlexTable);
		mainHorzPanel.add(rightVertPanel);
		rightVertPanel.add(TitleHorzPanel);
		TitleHorzPanel.add(titleLabel);
		TitleHorzPanel.add(loginButton);
		rightVertPanel.add(SearchPanel);
		
		// Set sizes for elements
		mainHorzPanel.setSize("100%", Window.getClientHeight() + "px");
		leftVertPanel.setSize("100%", "100%");
		rightVertPanel.setSize("100%",  "100%");
		mapPanel.setSize("100%", "100%");
		
		// Give panels borders for debugging purposes
		mainHorzPanel.setBorderWidth(5);
		leftVertPanel.setBorderWidth(5);
		rightVertPanel.setBorderWidth(5);
		mapPanel.setBorderWidth(5);

		// Set up map options
		MapOptions options  = MapOptions.create() ;
		options.setCenter(LatLng.create(49.251, 123.119));   
		options.setZoom(8) ;
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
		resultsFlexTable.setText(row, 0, parkingLoc.getParkingID());
		resultsFlexTable.setText(row, 1, Double.toString(parkingLoc.getPrice()));
		resultsFlexTable.setText(row, 2, Double.toString(parkingLoc.getLimit()));
	}
}