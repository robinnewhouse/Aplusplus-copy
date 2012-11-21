package ca.ubc.cpsc310.parkme.client.services.parking;

import javax.jdo.PersistenceManager;

import ca.ubc.cpsc310.parkme.client.NotLoggedInException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("fave")
public interface Fave extends RemoteService{
	public void addFave(String parkingID) throws  NotLoggedInException;
	public String[] getFaves() throws NotLoggedInException;
	public boolean checkFave(String parkingID) throws NotLoggedInException;
	public void removeFave(String parkingID) throws  NotLoggedInException;
	FaveStats[] getMostFaved();
}
