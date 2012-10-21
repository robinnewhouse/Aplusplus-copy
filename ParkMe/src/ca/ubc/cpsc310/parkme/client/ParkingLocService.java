package ca.ubc.cpsc310.parkme.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("park")
public interface ParkingLocService extends RemoteService{
	public ParkingLocation getParking(String id);
}
