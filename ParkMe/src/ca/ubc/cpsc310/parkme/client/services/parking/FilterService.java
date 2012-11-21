package ca.ubc.cpsc310.parkme.client.services.parking;

import ca.ubc.cpsc310.parkme.client.services.user.Criteria;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("filter")
public interface FilterService extends RemoteService{
	public ParkingLocation[] getParking(Criteria crit);

}