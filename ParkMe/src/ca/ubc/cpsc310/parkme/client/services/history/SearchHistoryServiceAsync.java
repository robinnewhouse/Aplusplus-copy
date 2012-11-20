package ca.ubc.cpsc310.parkme.client.services.history;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearchHistoryServiceAsync {
	void addSearchString(String str, AsyncCallback<Void> callback);

	void getHist(AsyncCallback<ArrayList<String>> callback);

	void clear(AsyncCallback<Void> asyncCallback);
}
