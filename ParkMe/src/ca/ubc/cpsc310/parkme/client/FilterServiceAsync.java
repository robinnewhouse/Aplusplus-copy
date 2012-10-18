package ca.ubc.cpsc310.parkme.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FilterServiceAsync {

	void getParking(Criteria crit, AsyncCallback<ParkingLocation[]> callback);

}
