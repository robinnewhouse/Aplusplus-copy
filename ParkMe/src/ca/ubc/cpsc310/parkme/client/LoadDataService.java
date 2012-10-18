package ca.ubc.cpsc310.parkme.client;

import ca.ubc.cpsc310.parkme.server.ParkingLoc;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("load")
public interface LoadDataService  extends RemoteService{
	public void loadData();
//	public void parseData();
	public ParkingLocation[] getParking();
}
