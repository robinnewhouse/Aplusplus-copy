package ca.ubc.cpsc310.parkme.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoadDataServiceAsync {

	public void loadData(AsyncCallback<Void> async);
	public void getParking(AsyncCallback<String[]> async);
}
