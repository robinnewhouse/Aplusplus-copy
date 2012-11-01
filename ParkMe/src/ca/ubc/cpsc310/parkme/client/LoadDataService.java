package ca.ubc.cpsc310.parkme.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("load")
public interface LoadDataService  extends RemoteService{
	public void loadData();
	public ParkingLocation[] getParking();
	public void setStreet(String street, String ID);
	public ParkingLocation[] getUnknownStreets();
	void setColor(String color, String parkingID);
}
