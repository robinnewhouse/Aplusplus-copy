package ca.ubc.cpsc310.parkme.client.services.parking;

import javax.jdo.PersistenceManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FaveAsync {

	void addFave(String parkingID, AsyncCallback<Void> callback);

	void getFaves(AsyncCallback<String[]> callback);

	void checkFave(String parkingID, AsyncCallback<Boolean> callback);

	void removeFave(String parkingID, AsyncCallback<Void> callback);

	void getMostFaved(AsyncCallback<ParkingStats[]> callback);



}
