package ca.ubc.cpsc310.parkme.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TicketServiceAsync {

	void addTicket(String parkingID, Double doubleAmount,
			AsyncCallback<Void> asyncCallback);

}
