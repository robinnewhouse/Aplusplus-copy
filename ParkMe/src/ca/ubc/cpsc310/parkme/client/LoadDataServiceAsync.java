package ca.ubc.cpsc310.parkme.client;

import ca.ubc.cpsc310.parkme.server.ParkingLoc;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoadDataServiceAsync {

	public void loadData(AsyncCallback<Void> async);
	void getParking(AsyncCallback<ParkingLocation[]> async);
}
