package ca.ubc.cpsc310.parkme.client;

import ca.ubc.cpsc310.parkme.shared.FieldVerifier;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
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

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ParkMe implements EntryPoint {
	
	
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
			VerticalPanel mapPanel = new VerticalPanel();  //TODO - Francis implement this properly - just reserving space now!
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		//TODO Make first row of Results Table the title
		RootPanel.get("parkMe").add(mainHorzPanel);
		mainHorzPanel.add(leftVertPanel);
			leftVertPanel.add(favoritesButton);
			leftVertPanel.add(historyButton);
			leftVertPanel.add(resultsFlexTable);
		mainHorzPanel.add(rightVertPanel);
			rightVertPanel.add(TitleHorzPanel);
				TitleHorzPanel.add(titleLabel);
				TitleHorzPanel.add(loginButton);
			rightVertPanel.add(SearchPanel);
			rightVertPanel.add(mapPanel);
		
	}
}