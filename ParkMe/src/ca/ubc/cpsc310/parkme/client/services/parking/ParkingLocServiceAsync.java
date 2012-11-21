package ca.ubc.cpsc310.parkme.client.services.parking;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ParkingLocServiceAsync {

	void getParking(String id, AsyncCallback<ParkingLocation> callback);

	void getParkings(String[] ids, AsyncCallback<ParkingLocation[]> callback);

}
