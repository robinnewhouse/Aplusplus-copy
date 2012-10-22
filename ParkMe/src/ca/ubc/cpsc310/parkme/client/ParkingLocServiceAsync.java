package ca.ubc.cpsc310.parkme.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ParkingLocServiceAsync {

	void getParking(String id, AsyncCallback<ParkingLocation> callback);

}
