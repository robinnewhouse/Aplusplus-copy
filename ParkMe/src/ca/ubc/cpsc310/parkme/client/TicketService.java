package ca.ubc.cpsc310.parkme.client;

import javax.jdo.PersistenceManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ticket")
public interface TicketService extends RemoteService{
	void addTicket(String parkingID, Double fine) throws NotLoggedInException;
	TicketInfo[] getTickets() throws NotLoggedInException;
}
