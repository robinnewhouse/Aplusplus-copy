package ca.ubc.cpsc310.parkme.client;

import javax.jdo.PersistenceManager;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ticket")
public interface TicketService extends RemoteService{

}
