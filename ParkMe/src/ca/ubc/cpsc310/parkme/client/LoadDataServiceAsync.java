package ca.ubc.cpsc310.parkme.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoadDataServiceAsync {

	public void loadData(AsyncCallback<Void> async);
	void getParking(AsyncCallback<ParkingLocation[]> async);
	void setStreet(String street, String ID, AsyncCallback<Void> callback);
	void getUnknownStreets(AsyncCallback<ParkingLocation[]> callback);
}
