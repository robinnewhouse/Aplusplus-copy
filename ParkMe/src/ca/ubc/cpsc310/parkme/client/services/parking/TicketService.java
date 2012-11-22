package ca.ubc.cpsc310.parkme.client.services.parking;

import javax.jdo.PersistenceManager;

import ca.ubc.cpsc310.parkme.client.services.user.NotLoggedInException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ticket")
public interface TicketService extends RemoteService{
	void addTicket(String parkingID, Double fine) throws NotLoggedInException;
	TicketInfo[] getTickets() throws NotLoggedInException;
	ParkingStats[] getMostTicketed();
	Long getNumTickets();
}