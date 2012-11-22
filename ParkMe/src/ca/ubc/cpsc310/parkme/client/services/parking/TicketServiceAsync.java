package ca.ubc.cpsc310.parkme.client.services.parking;


import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TicketServiceAsync {

	void addTicket(String parkingID, Double fine, AsyncCallback<Void> callback);

	void getTickets(AsyncCallback<TicketInfo[]> callback);

	void getMostTicketed(AsyncCallback<ParkingStats[]> callback);

	void getNumTickets(AsyncCallback<Long> callback);

}
