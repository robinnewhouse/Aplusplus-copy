package ca.ubc.cpsc310.parkme.client.services.parking;

import ca.ubc.cpsc310.parkme.client.services.user.Criteria;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FilterServiceAsync {

	void getParking(Criteria crit, AsyncCallback<ParkingLocation[]> callback);

}
