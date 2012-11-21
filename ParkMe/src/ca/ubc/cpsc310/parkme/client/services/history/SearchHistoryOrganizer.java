package ca.ubc.cpsc310.parkme.client.services.history;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;

//The intention of this class is to put most of the code 
//related to the search history in one class
public class SearchHistoryOrganizer {
	// Variables to get through constructor
	private final FlexTable histFlexTable;
	private final MultiWordSuggestOracle oracle;

	// Variables to create here
	private final ArrayList<String> searchHistList = new ArrayList<String>();
	private final SearchHistoryServiceAsync searchHistoryService = GWT
			.create(SearchHistoryService.class);

	public SearchHistoryOrganizer(FlexTable flexTableToUpdate,
			MultiWordSuggestOracle oracle) {
		this.histFlexTable = flexTableToUpdate;
		this.oracle = oracle;
	}

	public void loadAndShowSearchHistory() {
		searchHistoryService.getHist(new AsyncCallback<ArrayList<String>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Error occurred trying to get search history from server:"
						+ caught.getMessage());
			}

			public void onSuccess(ArrayList<String> result) {
				for (String search : result)
					addSearch(search);
				System.out
						.println("Finishing SearchHistoryOrganizer.loadAndShowSearchHistory-onSuccess: ");
			}
		});
		System.out
				.println("Finishing SearchHistoryOrganizer.loadAndShowSearchHistory");
	}

	public void addAndSaveSearch(String search) {
		System.out.println("Starting SearchHistoryOrganizer.addAndSaveSearch");
		// Decided to have a good history it was good to include the
		// multiplicity of searchs as well
		// if(!searchHistList.contains(search)){
		addSearch(search);
		saveSearch(search);
		// }
	}

	private void addSearch(String search) {
		searchHistList.add(search);
		oracle.add(search);
		int rows = histFlexTable.getRowCount();
		histFlexTable.setText(rows, 0, search);
	}

	private void saveSearch(String search) {
		searchHistoryService.addSearchString(search, new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to add search string to history: "
						+ caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				// No Need to do anything
			}
		});
	}

	public void clearHistory() {
		searchHistoryService.clear(new AsyncCallback<Void>() {
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed to clear search history: "
						+ caught.getMessage());
			}

			@Override
			public void onSuccess(Void result) {
				System.out
						.println("In Callback - Successfully Cleared History on Server");
				int rows = histFlexTable.getRowCount();
				for (int i = 0; i < rows; ++i) {
					histFlexTable.removeRow(0);
				}
				// histFlexTable.clear();
				searchHistList.clear();
				oracle.clear();
				System.out.println("In Callback - Cleared History Locally: "
						+ searchHistList.size());
			}
		});
	}
}
