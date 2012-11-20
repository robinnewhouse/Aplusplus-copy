package ca.ubc.cpsc310.parkme.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TicketServiceAsync {

	void addTicket(String parkingID, Double fine, AsyncCallback<Void> callback);

	void getTickets(AsyncCallback<TicketInfo[]> callback);

}
