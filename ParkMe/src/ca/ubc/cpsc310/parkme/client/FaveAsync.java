package ca.ubc.cpsc310.parkme.client;

import javax.jdo.PersistenceManager;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FaveAsync {

	void addFave(String parkingID, AsyncCallback<Void> callback);

	void getFaves(AsyncCallback<String[]> callback);



}
