package ca.ubc.cpsc310.parkme.client.services.parking;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoadDataServiceAsync {

	public void loadData(AsyncCallback<Void> async);
	void getParking(AsyncCallback<ParkingLocation[]> async);
	void setStreet(String street, String ID, AsyncCallback<Void> callback);
	void getUnknownStreets(AsyncCallback<ParkingLocation[]> callback);
	public void setColor(String color, String parkingID,
			AsyncCallback<Void> asyncCallback);
}
