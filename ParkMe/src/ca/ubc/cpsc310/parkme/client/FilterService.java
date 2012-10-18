package ca.ubc.cpsc310.parkme.client;

import com.google.gwt.user.client.rpc.RemoteService;

public interface FilterService extends RemoteService{
	public ParkingLocation[] getParking();

}
